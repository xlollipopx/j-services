package com.apigateway.oauth;

import com.apigateway.oauth.model.AuthenticationPayload;
import com.apigateway.oauth.model.AuthenticationStatus;
import com.apigateway.oauth.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final PersonService personService;

    @SneakyThrows
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info("In success handler{}", authentication);
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = token.getPrincipal();
        String mail = token.getPrincipal().getAttribute("email");
        Mono<Person> person = (personService.findByUsername(mail)).switchIfEmpty(personService.save(Person.builder()
                        .name(user.getAttribute("name"))
                        .username(mail)
                        .picture(user.getAttribute("picture"))
                .build()));

        String payload = objectMapper
                .writeValueAsString(
                        new AuthenticationPayload(AuthenticationStatus.SUCCESS.getValue())
                );

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        DataBuffer buffer = response.bufferFactory().wrap(payload.getBytes());
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return person.then(response.writeWith(Flux.just(buffer)));
    }
}
