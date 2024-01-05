package com.authservice.controller;

import com.authservice.controller.util.CookieUtil;
import com.authservice.dto.LoginRequest;
import com.authservice.model.Person;
import com.authservice.service.JwtService;
import com.authservice.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.authservice.controller.exception.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth-service")
public class AuthController {
    private final PersonService personService;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;

    @GetMapping("/testauth")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Test auth");
    }

    @PostMapping("/testLogin")
    public ResponseEntity<?> testLogin(@RequestBody LoginRequest request) {
        Person person = personService.getPersonByUserName(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = jwtService.generateAccessToken(person);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid LoginRequest request) {
        Optional<Person> person = personService.getPersonByUserName(request.getUsername());
        if (person.isPresent()) {
            throw new UserAlreadyExistsException();
        }
        personService.savePerson(request.getUsername(), request.getPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        Person person = personService.getPersonByUserName(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!DigestUtils.sha256Hex(request.getPassword()).equals(person.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials.");
        }
        String token = jwtService.generateAccessToken(person);
        return composeLogin(token);
    }

    @GetMapping("/validate/token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        log.info(token);
        if (!jwtService.validateAccessToken(token.substring(7))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ControllerUtils.buildResponse(jwtService.getPersonInfoFromToken(token.substring(7)), HttpStatus.OK);
    }

    private ResponseEntity<?> composeLogin(String accessToken) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(accessToken).toString());
        return ResponseEntity.ok().headers(responseHeaders).build();
    }

}
