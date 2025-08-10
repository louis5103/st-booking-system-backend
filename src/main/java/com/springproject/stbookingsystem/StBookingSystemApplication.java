package com.springproject.stbookingsystem;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class StBookingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StBookingSystemApplication.class, args);

        System.out.println("\n" +
                "=================================================\n" +
                "  ST 통합예매관리시스템이 성공적으로 시작되었습니다!\n" +
                "  서버 포트: 8080\n" +
                "  H2 콘솔: http://localhost:8080/h2-console\n" +
                "  API 문서: http://localhost:8080/api\n" +
                "=================================================\n");
    }
}