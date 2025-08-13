package com.hp.bingo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {


    @GetMapping("home")
    public String home() {
        log.info("Home page requested");
        return "home";
    }

    @GetMapping("admin")
    public String adminHome() {
        log.info("Admin home page requested");
        return "admin-home";
    }
}
