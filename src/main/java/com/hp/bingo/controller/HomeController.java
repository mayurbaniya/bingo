package com.hp.bingo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    @Value("${app.baseUrl}")
    private String baseUrl;

    @GetMapping({"home",""})
    public String home(Model model) {
        log.info("Home page requested");
        model.addAttribute("baseUrl", baseUrl);
        return "home";
    }

    @GetMapping("admin")
    public String adminHome() {
        log.info("Admin home page requested");
        return "admin-home";
    }
    @GetMapping("gallery")
    public String gallery() {
        log.info("Gallery page requested");
        return "gallery";
    }
}
