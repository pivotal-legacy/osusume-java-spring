package com.tokyo.beach.application;

import com.tokyo.beach.restaurants.filter.RequestFilter;
import com.tokyo.beach.restaurants.session.SessionTokenGenerator;
import com.tokyo.beach.restaurants.session.TokenGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.tokyo.beach")
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
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
