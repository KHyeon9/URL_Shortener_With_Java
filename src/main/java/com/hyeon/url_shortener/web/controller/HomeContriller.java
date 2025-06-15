package com.hyeon.url_shortener.web.controller;

import com.hyeon.url_shortener.ApplicationProperties;
import com.hyeon.url_shortener.domain.exception.ShortUrlNotFoundException;
import com.hyeon.url_shortener.domain.model.CreateShortUrlCmd;
import com.hyeon.url_shortener.domain.model.ShortUrlDto;
import com.hyeon.url_shortener.web.dto.CreateShortUrlForm;
import com.hyeon.url_shortener.service.ShortUrlService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeContriller {

    private final ShortUrlService shortUrlService;
    private final SecurityUtils securityUtils;
    private final ApplicationProperties properties;

    public HomeContriller(ShortUrlService shortUrlService, SecurityUtils securityUtils, ApplicationProperties properties) {
        this.shortUrlService = shortUrlService;
        this.securityUtils = securityUtils;
        this.properties = properties;
    }

    // 홈 화면으로 이동
    @GetMapping("/")
    public String home(Model model) {
        securityUtils.getCurrentUser();

        List<ShortUrlDto> shortUrls = shortUrlService.findAllPublicShortUrls();
        model.addAttribute("shortUrls", shortUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("createShortUrlForm",
                new CreateShortUrlForm("", false, null));
        return "index";
    }

    // 로그인 페이지
    @GetMapping("/login")
    String loginForm() {
        return "login";
    }

    // short url 생성
    @PostMapping("/short-urls")
    String createShortUrl(
            @ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // 오류가 있는지 확인
        if (bindingResult.hasErrors()) {
            // index 페이지에 필요한 값들을 설정
            List<ShortUrlDto> shortUrls = shortUrlService.findAllPublicShortUrls();
            model.addAttribute("shortUrls", shortUrls);
            model.addAttribute("baseUrl", properties.baseUrl());
            return "index";
        }

        try {
            Long userId = securityUtils.getCurrentUserId();
            CreateShortUrlCmd cmd = new CreateShortUrlCmd(
                    form.originalUrl(),
                    form.isPrivate(),
                    form.expirationInDays(),
                    userId
            );
            ShortUrlDto shortUrlDto = shortUrlService.createShortUrl(cmd);
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Short Url이 정상적으로 만들어 졌습니다. " +
                            properties.baseUrl() + "/s/" + shortUrlDto.shortKey());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Short Url을 만드는데 실패했습니다.");
        }
        return "redirect:/";
    }

    @GetMapping("/s/{shortKey}")
    String redirectToOriginalUrl(@PathVariable("shortKey") String shortKey) {
        Long userId = securityUtils.getCurrentUserId();
        Optional<ShortUrlDto> shortUrlDtoOptional = shortUrlService.accessShortUrl(shortKey, userId);

        if (shortUrlDtoOptional.isEmpty()) {
            throw new ShortUrlNotFoundException("유효하지 않은 Short Key 입니다: " + shortKey);
        }

        ShortUrlDto shortUrlDto = shortUrlDtoOptional.get();

        return "redirect:" + shortUrlDto.originalUrl();
    }
}
