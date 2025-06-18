package com.hyeon.url_shortener.web.controller;

import com.hyeon.url_shortener.ApplicationProperties;
import com.hyeon.url_shortener.domain.model.PagedResult;
import com.hyeon.url_shortener.domain.model.ShortUrlDto;
import com.hyeon.url_shortener.service.ShortUrlService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
@Controller
public class AdminController {
    private final ShortUrlService shortUrlService;
    private final ApplicationProperties properties;

    public AdminController(ShortUrlService shortUrlService, ApplicationProperties properties) {
        this.shortUrlService = shortUrlService;
        this.properties = properties;
    }

    // admin 페이지 dashboard 조회
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(defaultValue = "1") int page,
            Model model
    ) {
        PagedResult<ShortUrlDto> allUrls =
                shortUrlService.findAllShortUrls(page, properties.pageSize());
        model.addAttribute("shortUrls", allUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("paginationUrl", "/admin/dashboard");
        return "admin-dashboard";
    }
}
