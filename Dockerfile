###############################
# 1) BUILD STAGE ― Maven 빌드  #
###############################
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

# 의존성 캐시
COPY pom.xml .mvn/ mvnw ./
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline

# 실제 소스 복사 및 빌드
COPY src ./src
RUN ./mvnw -q -B clean package -DskipTests


###############################
# 2) RUNTIME STAGE ― 경량 JRE  #
###############################
# ❗️Slim 태그 대신 17-jre(bookworm) 사용
FROM eclipse-temurin:17-jre-bookworm

# 2-1) Python & Ultralytics (필수 최소 패키지만)
RUN apt-get update && \
    apt-get install -y --no-install-recommends python3 python3-pip && \
    pip3 install --no-cache-dir ultralytics==8.2.0 opencv-python-headless && \
    rm -rf /var/lib/apt/lists/* /root/.cache

# 2-2) 애플리케이션 복사
WORKDIR /app
COPY --from=builder /workspace/target/demo-*.jar app.jar
COPY scripts/ ./scripts/

# 2-3) 환경변수 / 메모리 상한
ENV PYTHON_EXE=/usr/bin/python3
ENV APP_UPLOAD_DIR=/app/uploads
ENV PORT=8080
ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx512m -XX:+UseSerialGC"
RUN mkdir -p $APP_UPLOAD_DIR

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
