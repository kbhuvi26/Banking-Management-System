package com.bms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Our frontend is a set of plain .html files, opened directly in the
 * browser or served by a separate static file server (e.g. VS Code
 * "Live Server" on port 5500). That makes it a different "origin" than
 * the backend (localhost:8080), so without this config the browser would
 * block every fetch() call with a CORS error.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
