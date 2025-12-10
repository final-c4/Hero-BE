package com.c4.hero.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * <pre>
 * Class Name : MailConfig
 * Description : Spring Mail 전송을 위한 기본 설정을 담당하는 클래스
 *
 * - Spring Boot의 Mail 자동설정 기반으로 JavaMailSender Bean을 생성
 * - application.yml의 spring.mail.* 설정값을 자동 반영하여 SMTP 환경 구성
 * - 메일 전송이 필요한 Service 계층에서 JavaMailSender 주입 가능
 *
 * History
 * 2025/12/10 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */

@Configuration
public class MailConfig {

    /**
     * JavaMailSender Bean 생성
     *
     * Spring Boot Mail AutoConfiguration에 의해
     * application.yml 설정이 JavaMailSenderImpl에 자동 적용됨
     *
     * @return JavaMailSender 메일 전송 객체
     */
    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }
}
