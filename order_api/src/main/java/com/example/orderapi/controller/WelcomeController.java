package com.example.orderapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public RedirectView welcome() {
        return new RedirectView("/swagger-ui/index.html");
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\":\"UP\",\"service\":\"Order API\"}";
    }
}
