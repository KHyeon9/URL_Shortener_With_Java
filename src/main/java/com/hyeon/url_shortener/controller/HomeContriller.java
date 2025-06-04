package com.hyeon.url_shortener.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeContriller {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "URL Shortener - Thymeleaf");
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
