package com.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
@Slf4j
@Component
@Order(-1)
public class CustomHeaderFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Filter started.");
        String requestPath = exchange.getRequest().getURI().getPath();

        if (requestPath.contains("/login")) {
            return chain.filter(exchange);
        }
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();
                    log.info("Authentication {}", authentication);
                    log.info("Req 1 {}: ", exchange.getRequest());
                    ServerWebExchange newExchange = exchange;
                    if (authentication != null && authentication.isAuthenticated()) {

                        log.info("Req 2 {}: ", exchange.getRequest());
                        newExchange = exchange.mutate().request(
                                        exchange.getRequest().mutate()
                                                .header("x-person-id", authentication.getCredentials().toString())
                                                .build())
                                .build();
                    }
                    return newExchange;
                })
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }
}