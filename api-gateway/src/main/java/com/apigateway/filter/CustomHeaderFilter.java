package com.apigateway.filter;

import com.apigateway.oauth.PersonService;
import com.apigateway.oauth.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(-1)
public class CustomHeaderFilter implements GlobalFilter {

    private final PersonService personService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Filter started.");
        String requestPath = exchange.getRequest().getURI().getPath();

        if (requestPath.contains("/login")) {
            return chain.filter(exchange);
        }

        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();
                    log.info("Authentication {}", authentication);
                    if (authentication instanceof OAuth2AuthenticationToken) {
                        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
                        return personService.findByUsername(token.getPrincipal().getAttribute("email"))
                                .map(p -> p.getPersonId().toString());
                    } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
                        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
                        return personService.findByUsername((String) token.getPrincipal())
                                .map(p -> p.getPersonId().toString());
                    }
                    return Mono.just(authentication.getCredentials().toString());
                })
                .flatMap(personId -> {
                    log.info("PersonId {}", personId);
                    log.info("Req 1 {}: ", exchange.getRequest());

                    ServerWebExchange newExchange = exchange;

                    if (personId != null) {
                        log.info("Req 2 {}: ", exchange.getRequest());
                        newExchange = exchange.mutate().request(
                                        exchange.getRequest().mutate()
                                                .header("x-person-id", personId)
                                                .build())
                                .build();
                    }

                    return chain.filter(newExchange);
                });
    }
}