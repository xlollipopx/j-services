package com.apigateway.config;


//import com.apigateway.filter.AuthenticationGatewayFilterFactory;
import com.apigateway.filter.CustomHeaderFilter;
import com.apigateway.security.AuthenticationManager;
import com.apigateway.security.SecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final CustomHeaderFilter filter;


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf()
                .disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers("/auth-service/login", "/auth-service/register", "/auth-service/validate/**", "/exchange-service/hello").permitAll()
                .pathMatchers("/exchange-service/test").authenticated()
                .pathMatchers("/exchange-service/admin-test").hasAuthority("ADMIN")
                .anyExchange()
                .permitAll()
                .and()
                .httpBasic()
                .disable()
                .formLogin()
                .disable();
        return http.build();
    }




}
