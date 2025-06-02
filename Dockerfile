###########################################
# 1) BUILD STAGE – Maven 빌드 (Java 앱 빌드)
###########################################
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

# 의존성 캐시
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# 전체 소스 복사 및 빌드
COPY src ./src
RUN mvn -q -B clean package -DskipTests

#############################################
# 2) BASE PYTHON IMAGE – Python + ultralytics 설치
#############################################
FROM eclipse-temurin:17-jre AS python-base

# 미리 설치해두고 캐시 재사용 가능하게 분리
RUN apt-get update && \
    apt-get install -y --no-install-recommends python3 python3-pip && \
    PIP_BREAK_SYSTEM_PACKAGES=1 \
    pip3 install --no-cache-dir ultralytics==8.2.0 opencv-python-headless && \
    rm -rf /var/lib/apt/lists/* /root/.cache

##################################################
# 3) FINAL STAGE – Java + Python 환경을 통합 배포
##################################################
FROM python-base
WORKDIR /app

# 빌드한 JAR 복사
COPY --from=builder /workspace/target/demo-*.jar app.jar
COPY scripts/ ./scripts/

# 환경 변수
ENV PYTHON_EXE=/usr/bin/python3
ENV APP_UPLOAD_DIR=/app/uploads
ENV PORT=8080
ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx512m -XX:+UseSerialGC"
RUN mkdir -p $APP_UPLOAD_DIR

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
