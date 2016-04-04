package com.tokyo.beach;

import com.tokyo.beach.application.RestControllerExceptionHandler;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import javax.sql.DataSource;
import java.lang.reflect.Method;

public class ControllerTestingUtils {
    public static ExceptionHandlerExceptionResolver createControllerAdvice(final RestControllerExceptionHandler handler) {

        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            @Override
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(handler.getClass())
                        .resolveMethod(exception);

                return new ServletInvocableHandlerMethod(handler, method);
            }
        };
        exceptionResolver.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        exceptionResolver.afterPropertiesSet();

        return exceptionResolver;
    }

    public static DataSource buildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost/osusume-test");
        return dataSource;
    }
}
