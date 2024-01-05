package com.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("auth-service", r -> r.path("/auth-service/**")
                        .uri("lb://auth-service"))
                .route("exchange-service", r -> r.path("/exchange-service/**")
                        .uri("lb://exchange-service"))
                .build();
    }

}
