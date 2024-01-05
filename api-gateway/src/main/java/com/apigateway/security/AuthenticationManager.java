package com.apigateway.security;

import com.apigateway.oauth.model.PersonInfo;
import com.apigateway.utils.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.security.sasl.AuthenticationException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if(authentication.getCredentials() == null) {
            return Mono.error(AuthenticationException::new);
        }
        String jwtToken = authentication.getCredentials().toString();
        return Mono.just(jwtService.getPersonInfoFromToken(jwtToken))
                .map(this::getAuthorities);
    }

    private UsernamePasswordAuthenticationToken getAuthorities(PersonInfo personInfo) {
        log.info("response: {}", personInfo);
        return new UsernamePasswordAuthenticationToken(
                personInfo.getUsername(), personInfo.getPersonId(),
                personInfo.getAuthorities().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
    }

}