###############################
# 1) BUILD STAGE ― Maven 빌드  #
###############################
FROM maven:3.9.6-eclipse-temurin-17 AS builder   # Maven + JDK 17 포함
WORKDIR /workspace

# 의존성 캐시 활용을 위해 pom.xml만 먼저 복사 → mvn dependency:go-offline
COPY pom.xml .mvn/ mvnw ./
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline

# 실제 소스 복사 후 빌드
COPY src ./src
RUN ./mvnw -q -B clean package -DskipTests

###############################
# 2) RUNTIME STAGE ― 경량 JRE  #
###############################
FROM eclipse-temurin:17-jre-slim        # JRE만 포함(≈ 90 MB)

# -------- 2-1) Python & Ultralytics 최소 설치 --------
RUN apt-get update && \
    apt-get install -y --no-install-recommends python3 python3-pip && \
    pip3 install --no-cache-dir ultralytics==8.2.0 opencv-python-headless && \
    # 클린업
    rm -rf /var/lib/apt/lists/* /root/.cache

# -------- 2-2) 앱 배치 --------
WORKDIR /app
COPY --from=builder /workspace/target/demo-*.jar app.jar
COPY scripts/ ./scripts/          # recycle.py, best.pt 등

# -------- 2-3) 환경변수/포트/메모리 제한 --------
ENV PYTHON_EXE=/usr/bin/python3
ENV APP_UPLOAD_DIR=/app/uploads
ENV PORT=8080
# Java 메모리 상한(필요에 따라 조정)
ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx512m -XX:+UseSerialGC"

RUN mkdir -p $APP_UPLOAD_DIR

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
