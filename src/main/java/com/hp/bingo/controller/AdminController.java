package com.hp.bingo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hp.bingo.dto.EventRequest;
import com.hp.bingo.dto.Response;
import com.hp.bingo.service.AdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/registrations")
    public ResponseEntity<Response> getAllRegistrations(
            @RequestParam(required = false) Boolean paymentConfirmed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "1") String status) {

        return ResponseEntity.ok(adminService.getAllRegistrations(paymentConfirmed, page, size, status));
    }

    @PostMapping("/confirm-payment/{id}")
    public ResponseEntity<Response> confirmPayment(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.confirmPayment(id));
    }

    @DeleteMapping("/delete-registration/{id}")
    public ResponseEntity<Response> deleteRegistration(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deleteRegistration(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchRegistrations(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.searchRegistrations(query, page, size));
    }

    @PostMapping("/add-event")
    public ResponseEntity<Response> addEvent(@RequestBody EventRequest request) {
        return ResponseEntity.ok(adminService.addEvent(request));
    }

}
