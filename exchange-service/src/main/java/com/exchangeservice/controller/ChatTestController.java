package com.exchangeservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange-service")
public class ChatTestController {

    @GetMapping("/test")
    public ResponseEntity<?> test(@RequestHeader("x-person-id") String personId) {
        log.info(personId);
        return ResponseEntity.ok("Test exchange service");
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello from exchange service");
    }

    @GetMapping("/admin-test")
    public ResponseEntity<?> adminTest() {
        return ResponseEntity.ok("Admin test endpoint");
    }

}
