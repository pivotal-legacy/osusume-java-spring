package com.tokyo.beach.application;

import com.tokyo.beach.application.filter.RequestFilter;
import com.tokyo.beach.application.session.SessionTokenGenerator;
import com.tokyo.beach.application.session.TokenGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OsusumeJavaSpringApplication {

    @Bean
    public RequestFilter requestFilter() {
        return new RequestFilter();
    }

    @Bean
    public TokenGenerator tokenGenerator() {
        return new SessionTokenGenerator();
    }

    public static void main(String[] args) {
        SpringApplication.run(OsusumeJavaSpringApplication.class, args);
    }

}
