package com.hp.bingo.utils;

import org.springframework.stereotype.Component;

import com.hp.bingo.constants.AppConstant;
import com.hp.bingo.dto.Response;

@Component
public class Utils {

    // === Utility Methods ===
    public boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[6-9]\\d{9}$");
    }

    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public Response errorResponse(String msg) {
        return Response.builder()
                .msg(msg)
                .status(AppConstant.ERROR)
                .build();
    }
}
