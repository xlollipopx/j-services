package com.apigateway.oauth;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomStatelessAuthorizationRequestResolver implements ServerOAuth2AuthorizationRequestResolver {

    private final ReactiveClientRegistrationRepository clientRegistrationRepository;
    private final ServerOAuth2AuthorizationRequestResolver delegate;
    public CustomStatelessAuthorizationRequestResolver(
            ReactiveClientRegistrationRepository clientRegistrationRepository
            ) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.delegate =  new DefaultServerOAuth2AuthorizationRequestResolver(this.clientRegistrationRepository);
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange) {
        return delegate.resolve(exchange);
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> resolve(ServerWebExchange exchange, String clientRegistrationId) {
        return delegate.resolve(exchange, clientRegistrationId);

    }
}
