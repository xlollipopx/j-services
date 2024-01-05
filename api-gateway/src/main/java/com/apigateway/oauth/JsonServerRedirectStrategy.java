package com.apigateway.oauth;

import com.apigateway.oauth.model.RedirectStrategyPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonServerRedirectStrategy implements ServerRedirectStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> sendRedirect(ServerWebExchange exchange, URI location) {
        log.info("Send redirect to {}", location);
        String payload = "";
        try {
            payload = serializeRedirectStrategy(new RedirectStrategyPayload(location.toString()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(payload.getBytes());
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    private String serializeRedirectStrategy(RedirectStrategyPayload redirectStrategyPayload) throws JsonProcessingException {
        return objectMapper.writeValueAsString(redirectStrategyPayload);
    }
}
