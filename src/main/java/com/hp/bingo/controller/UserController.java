package com.hp.bingo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hp.bingo.dto.Request;
import com.hp.bingo.dto.Response;
import com.hp.bingo.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody Request request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping(value = "/add-payment-proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> addPaymentProof(
            @RequestParam String email,
            @RequestParam String phone,
            @RequestPart MultipartFile file) {
        return ResponseEntity.ok(userService.addPaymentProof(email, phone, file));
    }

    @PostMapping("/details")
    public ResponseEntity<Response> getDetail(@RequestParam String registrationId) {
        return ResponseEntity.ok(userService.getRegistrationDetails(registrationId));
    }
}
