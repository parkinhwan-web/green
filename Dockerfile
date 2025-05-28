###############################
# 1) BUILD STAGE – Maven 빌드 #
###############################
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

# 1-A) 의존성 캐시
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# 1-B) 전체 소스 복사 후 패키징
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# 2) RUNTIME STAGE – eclipse-temurin:17-jre (bookworm, Python 3.12)
FROM eclipse-temurin:17-jre

RUN apt-get update && \
    apt-get install -y --no-install-recommends python3 python3-pip && \
    # 🔸 PEP 668 우회 플래그
    PIP_BREAK_SYSTEM_PACKAGES=1 \
    pip3 install --no-cache-dir ultralytics==8.2.0 opencv-python-headless && \
    rm -rf /var/lib/apt/lists/* /root/.cache

# 2-2) 애플리케이션 배치
WORKDIR /app
COPY --from=builder /workspace/target/demo-*.jar app.jar
COPY scripts/ ./scripts/

# 2-3) ENV · 메모리 상한
ENV PYTHON_EXE=/usr/bin/python3
ENV APP_UPLOAD_DIR=/app/uploads
ENV PORT=8080
ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx512m -XX:+UseSerialGC"
RUN mkdir -p $APP_UPLOAD_DIR

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
