package com.apigateway.config;

import com.apigateway.filter.JwtCustomFilter;
import com.apigateway.oauth.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomStatelessAuthorizationRequestRepository customStatelessAuthorizationRequestRepository;
    private final CustomStatelessAuthorizationRequestResolver customStatelessAuthorizationRequestResolver;
    private final ReactiveOAuth2AuthorizedClientService customAuthorizedClientService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final CustomRedirectFilter customRedirectFilter;
    private final JwtCustomFilter jwtCustomFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http
                .cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                .authorizeExchange()
                .pathMatchers("/exchange-service/test").authenticated()
                .pathMatchers("/auth/**", "/oauth2/**", "/login/oauth2/code/**", "/auth-service/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2Login(x -> x
                        .authorizationRequestRepository(customStatelessAuthorizationRequestRepository)
                        .authorizationRequestResolver(customStatelessAuthorizationRequestResolver)
                        .authorizedClientService(customAuthorizedClientService)
                        .authenticationSuccessHandler(oAuth2AuthenticationSuccessHandler)
                        .authenticationFailureHandler(oAuth2AuthenticationFailureHandler))
                .exceptionHandling(x -> x.accessDeniedHandler(accessDeniedHandler())
                        .authenticationEntryPoint(authenticationEntryPoint()))
                .addFilterAt(jwtCustomFilter, SecurityWebFiltersOrder.HTTP_BASIC)
                .addFilterAt(customRedirectFilter, SecurityWebFiltersOrder.HTTP_BASIC)
        ;

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public ServerAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
}
