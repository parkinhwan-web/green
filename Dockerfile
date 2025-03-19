# OpenJDK 17 기반 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너에서 실행할 포트 노출
EXPOSE 8081

# Spring Boot 실행 명령어
CMD ["java", "-jar", "app.jar"]
