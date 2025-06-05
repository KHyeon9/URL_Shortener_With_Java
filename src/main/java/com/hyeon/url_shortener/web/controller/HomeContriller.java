package com.hyeon.url_shortener.web.controller;

import com.hyeon.url_shortener.domain.entity.ShortUrl;
import com.hyeon.url_shortener.service.ShortUrlService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeContriller {

    private final ShortUrlService shortUrlService;

    public HomeContriller(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<ShortUrl> shortUrls = shortUrlService.findAllPublicShortUrls();
        model.addAttribute("shortUrls", shortUrls);
        model.addAttribute("baseUrl", "http://localhost:8080");
        return "index";
    }

}
