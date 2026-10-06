package com.banco.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Habilita el consumo de la API desde el frontend. Los orígenes permitidos
 * se parametrizan para no atar la configuración a un ambiente.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${aplicacion.cors.origenes-permitidos:http://localhost:4200,http://localhost}")
    private String[] origenesPermitidos;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(origenesPermitidos)
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
            .allowedHeaders("*");
    }
}
