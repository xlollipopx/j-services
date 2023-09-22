package com.apigateway.config;

//import com.apigateway.filter.AuthenticationGatewayFilterFactory;
import com.apigateway.filter.CustomHeaderFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
