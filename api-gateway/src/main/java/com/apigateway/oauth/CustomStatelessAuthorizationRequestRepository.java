package com.apigateway.oauth;

import com.apigateway.oauth.model.AuthRequestHolder;
import com.apigateway.oauth.repository.AuthRequestHolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.*;


@Component
@RequiredArgsConstructor
public class CustomStatelessAuthorizationRequestRepository implements ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final AuthRequestHolderRepository authRequestHolderRepository;

    private static final String AUTH_ID_HEADER = "Auth-Identification";
    private static final Base64.Encoder B64E = Base64.getEncoder();
    private static final  Base64.Decoder B64D = Base64.getDecoder();

    @Override
    public Mono<OAuth2AuthorizationRequest> loadAuthorizationRequest(ServerWebExchange exchange) {
        String authId = exchange.getRequest().getHeaders().getFirst(AUTH_ID_HEADER);
        if(authId == null) {
            return Mono.empty();
        }
        return authRequestHolderRepository
                .findByid(UUID.fromString(authId))
                .map(holder -> decrypt(holder.getPayload()));
    }

    @Override
    public Mono<Void> saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, ServerWebExchange exchange) {
        if (authorizationRequest == null) {
            exchange.getResponse().getHeaders().remove(AUTH_ID_HEADER);
            return Mono.empty();
        } else {
            AuthRequestHolder authRequestHolder
                    = new AuthRequestHolder(UUID.randomUUID(), encrypt(authorizationRequest));
            exchange.getResponse().getHeaders().set(AUTH_ID_HEADER, authRequestHolder.getUuid().toString());
            return authRequestHolderRepository.save(authRequestHolder).then();
        }
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> removeAuthorizationRequest(ServerWebExchange exchange) {
        String authId = exchange.getRequest().getHeaders().getFirst(AUTH_ID_HEADER);
        if (authId == null) {
            return Mono.empty();
        }

        return authRequestHolderRepository.findByid(UUID.fromString(authId)).flatMap(holder -> authRequestHolderRepository.remove(holder.getUuid()).thenReturn(decrypt(holder.getPayload())));
    }

    private String encrypt(OAuth2AuthorizationRequest authorizationRequest) {
        byte[] bytes = SerializationUtils.serialize(authorizationRequest);
        return B64E.encodeToString(bytes);
    }

    private OAuth2AuthorizationRequest decrypt(String encrypted) {
        byte[] encryptedBytes = B64D.decode(encrypted);
        return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(encryptedBytes);
    }
}
