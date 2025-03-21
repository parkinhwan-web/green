package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        // ✅ Render의 자동 포트 할당을 무시하고, 8080으로 강제 설정
        System.setProperty("server.port", "8080");

        // Spring Boot 애플리케이션 실행
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

        // 환경 변수에서 PORT 값 가져오기
        Environment env = context.getEnvironment();
        String port = env.getProperty("PORT"); // Render 환경 변수에서 PORT 가져오기
        String serverPort = env.getProperty("server.port"); // Spring 설정에서 server.port 가져오기

        // 현재 설정된 포트 출력
        System.out.println("✅ 현재 설정된 환경 변수 PORT: " + port);
        System.out.println("✅ 현재 설정된 server.port: " + serverPort);
    }
}
