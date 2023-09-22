package com.authservice.controller;

import com.authservice.controller.util.CookieUtil;
import com.authservice.dto.LoginRequest;
import com.authservice.model.Person;
import com.authservice.service.JwtService;
import com.authservice.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth-service")
public class AuthController {
    private final PersonService personService;
    private final JwtService jwtService;
  //  private final AuthenticationManager authenticationManager;
    private final CookieUtil cookieUtil;

    @GetMapping("/getUsers")
    public ResponseEntity<?> testAdmin() {
        return ResponseEntity.ok("Users list:");
    }

    @GetMapping("/testauth")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Test auth");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        personService.savePerson(request.getUsername(), request.getPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate/token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        log.info(token);
        if(!jwtService.validateAccessToken(token.substring(7))) {
           return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ControllerUtils.buildResponse(jwtService.getPersonInfoFromToken(token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
       // authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Person person = personService.getPersonByUserName(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = jwtService.generateAccessToken(person);
        return ResponseEntity.ok(token);//composeLogin(token);
    }


    private ResponseEntity<?> composeLogin(String accessToken) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(accessToken).toString());
        return ResponseEntity.ok().headers(responseHeaders).build();
    }

}
