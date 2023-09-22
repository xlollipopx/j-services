package com.apigateway.security;

import com.apigateway.dto.ResponseDTO;
import com.apigateway.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Lazy
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final Builder webClient;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String jwtToken = authentication.getCredentials().toString();
        return tokenValidate(jwtToken)
                .bodyToMono(new ParameterizedTypeReference<ResponseDTO<PersonInfo>>(){})
                .map(x -> this.getAuthorities(x.getData()));
    }

    private UsernamePasswordAuthenticationToken getAuthorities(PersonInfo personInfo) {
        log.info("RESPONSE: {}", personInfo);
        return new UsernamePasswordAuthenticationToken(
                personInfo.getUsername(), personInfo.getPersonId(),
                personInfo.getAuthorities().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
    }

    private WebClient.ResponseSpec tokenValidate(String token) {
        return webClient.build()
                .get()
                .uri("http://localhost:8083/auth-service/validate/token?token=" + token)//uriBuilder -> uriBuilder.host("auth-service").path("/validate-token").queryParam("token", token).build())
                .retrieve()
                .onStatus(HttpStatus.FORBIDDEN::equals, response -> Mono.error(new InvalidTokenException("Token is not valid")));
    }
}