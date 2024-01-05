package com.apigateway.filter;

import com.apigateway.utils.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(-2)
public class JwtCustomFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveAuthenticationManager authenticationManager;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        var tokenCookie = exchange.getRequest().getCookies().getFirst("access_token");
        String principal = null;
        String token = null;
        if (tokenCookie != null) {
            principal = jwtService.getUsername(tokenCookie.getValue());
            token = tokenCookie.getValue();
        }

        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(principal, token))
                .flatMap(authentication -> chain.filter(exchange).contextWrite(c -> ReactiveSecurityContextHolder.withAuthentication(authentication)))
                .onErrorResume(Exception.class, e -> {
                    return chain.filter(exchange);
                });
    }
}
