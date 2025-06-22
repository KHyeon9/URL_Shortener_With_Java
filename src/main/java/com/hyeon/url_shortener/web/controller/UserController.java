package com.hyeon.url_shortener.web.controller;

import com.hyeon.url_shortener.domain.model.CreateUserCmd;
import com.hyeon.url_shortener.domain.model.Role;
import com.hyeon.url_shortener.service.UserService;
import com.hyeon.url_shortener.web.dto.RegisterUserRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute(
                "user",
                new RegisterUserRequest("", "", "")
        );
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") RegisterUserRequest registerRequest,
           BindingResult bindingResult,
           RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            CreateUserCmd cmd = new CreateUserCmd(
                    registerRequest.email(),
                    registerRequest.password(),
                    registerRequest.name(),
                    Role.ROLE_USER
            );

            userService.createUser(cmd);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "회원가입 성공! 로그인해 주세요."
            );

            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "회원가입 실패: " + e.getMessage()
            );
        }
        return "redirect:/register";
    }
}
