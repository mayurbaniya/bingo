package com.hp.bingo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Value("${app.bingo.first_row}")
    private int firstRowPrize;

    @Value("${app.bingo.event_date}")
    private String eventDate;

    @Value("${app.bingo.second_row}")
    private int secondRowPrize;

    @Value("${app.bingo.third_row}")
    private int thirdRowPrize;

    @Value("${app.bingo.full_house}")
    private int fullHousePrize;

    @Value("${app.bingo.first_five}")
    private int firstFivePrize;

    @Value("${app.payment.per-ticket-price}")
    private int perTicketPrice;

    @Value("${app.contact.pendol_address}")
    private String pendolAddress;

    @Value("${app.contact.pendol_location}")
    private String pendolLocation;

    @Value("${app.contact.pendol_committee}")
    private String pendolCommittee;

    @Value("${app.contact.phone_number}")
    private String phoneNumber;

    @Value("${app.contact.email}")
    private String email;

    @Value("${app.contact.eventsTiming}")
    private String eventsTiming;

    @Value("${app.developer1.name}")
    private String developer1Name;

    @Value("${app.developer1.email}")
    private String developer1Email;

    @Value("${app.developer1.insta}")
    private String developer1Insta;

    @Value("${app.developer1.experience}")
    private String developer1Experience;

    @Value("${app.developer2.name}")
    private String developer2Name;

    @Value("${app.developer2.email}")   
    private String developer2Email;

    @Value("${app.developer2.insta}")
    private String developer2Insta;

    @Value("${app.developer2.experience}")
    private String developer2Experience;

    @GetMapping({ "home", "" })
    public String home(Model model) {
        log.info("Home page requested");
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("firstRowPrize", firstRowPrize);
        model.addAttribute("secondRowPrize", secondRowPrize);
        model.addAttribute("thirdRowPrize", thirdRowPrize);
        model.addAttribute("fullHousePrize", fullHousePrize);
        model.addAttribute("perTicketPrice", perTicketPrice);
        model.addAttribute("pendolAddress", pendolAddress);
        model.addAttribute("pendolCommittee", pendolCommittee);
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("email", email);
        model.addAttribute("eventsTiming", eventsTiming);
        model.addAttribute("developer1Name", developer1Name);
        model.addAttribute("developer1Email", developer1Email);
        model.addAttribute("developer1Insta", developer1Insta);
        model.addAttribute("developer1Experience", developer1Experience);
        model.addAttribute("developer2Name", developer2Name);
        model.addAttribute("developer2Email", developer2Email);
        model.addAttribute("developer2Insta", developer2Insta);
        model.addAttribute("developer2Experience", developer2Experience);
        model.addAttribute("firstFivePrize", firstFivePrize);
        model.addAttribute("eventDate", eventDate);
        return "home";
    }

    @GetMapping({"admin/dashboard","admin"})
    public String adminHome() {
        log.info("Admin home page requested");
        return "admin-home";
    }

    @GetMapping("gallery")
    public String gallery(Model model) {
        log.info("Gallery page requested");
  model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("firstRowPrize", firstRowPrize);
        model.addAttribute("secondRowPrize", secondRowPrize);
        model.addAttribute("thirdRowPrize", thirdRowPrize);
        model.addAttribute("fullHousePrize", fullHousePrize);
        model.addAttribute("perTicketPrice", perTicketPrice);
        model.addAttribute("pendolAddress", pendolAddress);
        model.addAttribute("pendolCommittee", pendolCommittee);
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("email", email);
        model.addAttribute("eventsTiming", eventsTiming);
        model.addAttribute("developer1Name", developer1Name);
        model.addAttribute("developer1Email", developer1Email);
        model.addAttribute("developer1Insta", developer1Insta);
        model.addAttribute("developer1Experience", developer1Experience);
        model.addAttribute("developer2Name", developer2Name);
        model.addAttribute("developer2Email", developer2Email);
        model.addAttribute("developer2Insta", developer2Insta);
        model.addAttribute("developer2Experience", developer2Experience);
        return "gallery";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        // Replace with real validation
        if ("admin".equals(username) && "andachowk".equals(password)) {
            session.setAttribute("isAdminLoggedIn", true);
            return "redirect:/admin/dashboard";
        }
        return "admin-login";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin-login"; // name of your Thymeleaf login HTML file (without .html)
    }


    @PostMapping("/admin/logout")
    @ResponseBody
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out successfully";
    }
}
