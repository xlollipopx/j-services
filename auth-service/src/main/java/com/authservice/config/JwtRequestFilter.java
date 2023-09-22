//package com.authservice.config;
//
//import com.authservice.service.JwtService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.Cookie;
//import java.io.IOException;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class JwtRequestFilter extends OncePerRequestFilter {
//    private final JwtService jwtService;
//    private static final String accessTokenCookieName = "access_token";
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//
//        if(authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            if(jwtService.validateAccessToken(token)
//                    && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UsernamePasswordAuthenticationToken upt
//                        = new UsernamePasswordAuthenticationToken(
//                                jwtService.getUsername(token),
//                        null,
//                        jwtService.getRoles(token).stream()
//                                .map(r -> new SimpleGrantedAuthority(r.getAuthority()))
//                                .collect(Collectors.toList()));
//                SecurityContextHolder.getContext().setAuthentication(upt);
//            }
//        } else {
//            String token = getJwtFromCookie(request);
//            if(token != null && jwtService.validateAccessToken(token)
//                    && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UsernamePasswordAuthenticationToken upt
//                        = new UsernamePasswordAuthenticationToken(
//                        jwtService.getUsername(token),
//                        null,
//                        jwtService.getRoles(token).stream()
//                                .map(r -> new SimpleGrantedAuthority(r.getAuthority()))
//                                .collect(Collectors.toList()));
//                SecurityContextHolder.getContext().setAuthentication(upt);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String getJwtFromCookie(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            if (accessTokenCookieName.equals(cookie.getName())) {
//                return cookie.getValue();
//            }
//        }
//        return null;
//    }
//
//}
