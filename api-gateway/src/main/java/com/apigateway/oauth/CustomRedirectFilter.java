package com.apigateway.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class CustomRedirectFilter implements WebFilter {

    private final CustomStatelessAuthorizationRequestResolver customStatelessAuthorizationRequestResolver;
    private final CustomStatelessAuthorizationRequestRepository customStatelessAuthorizationRequestRepository;
    private final ServerRedirectStrategy jsonServerRedirectStrategy;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
      return customStatelessAuthorizationRequestResolver
                .resolve(exchange)
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                .onErrorResume(ClientAuthorizationRequiredException.class,
                        e -> customStatelessAuthorizationRequestResolver
                                .resolve(exchange, e.getClientRegistrationId()))
                .flatMap(x -> handleAuth(exchange, x));
    }

    public Mono<Void> handleAuth(ServerWebExchange exchange, OAuth2AuthorizationRequest authorizationRequest) {
        Mono<Void> saveAuthorizationRequest;

        if (AuthorizationGrantType.AUTHORIZATION_CODE == authorizationRequest.getGrantType()) {
            saveAuthorizationRequest = this.customStatelessAuthorizationRequestRepository
                    .saveAuthorizationRequest(authorizationRequest, exchange);
        } else {
            saveAuthorizationRequest = Mono.empty();
        }


        URI redirectUri =
                UriComponentsBuilder.fromUriString(authorizationRequest.getAuthorizationRequestUri())
                        .build(true)
                        .toUri();

        return Mono.defer(() -> saveAuthorizationRequest.then(jsonServerRedirectStrategy.sendRedirect(exchange, redirectUri)));
    }
}
