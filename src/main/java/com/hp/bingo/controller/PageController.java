package com.hp.bingo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.bingo.dto.Response;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
public class PageController {

    @Value("${app.baseUrl}")
    private String baseUrl;

    @GetMapping({ "home", "" })
    public String home(Model model) {
        log.info("Home page requested");
        model.addAttribute("baseUrl", baseUrl);
        return "home";
    }

    @GetMapping({"admin/dashboard","admin"})
    public String adminHome() {
        log.info("Admin home page requested");
        return "admin-home";
    }

    @GetMapping("gallery")
    public String gallery() {
        log.info("Gallery page requested");
        return "gallery";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        // Replace with real validation
        if ("admin".equals(username) && "admin".equals(password)) {
            session.setAttribute("isAdminLoggedIn", true);
            return "redirect:/admin/dashboard";
        }
        return "admin-login";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin-login"; // name of your Thymeleaf login HTML file (without .html)
    }


    @GetMapping("/admin/logout")
        public String logout(HttpSession session) {
            session.invalidate();
            return "redirect:/admin/login";
    }
}
