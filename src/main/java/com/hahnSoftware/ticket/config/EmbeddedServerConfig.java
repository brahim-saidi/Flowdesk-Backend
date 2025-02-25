package com.hahnSoftware.ticket.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedServerConfig {
    
    private static final int SERVER_PORT = 8080;
    
    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.setPort(SERVER_PORT);
        factory.setContextPath("");
        return factory;
    }
}