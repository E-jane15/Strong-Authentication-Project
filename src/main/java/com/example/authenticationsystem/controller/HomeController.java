package com.example.authenticationsystem.controller;

import com.example.authenticationsystem.entity.User;
import com.example.authenticationsystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final UserService service;

    public HomeController(UserService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, Model model) {

        String response = service.register(user);

        if (response.equals("SUCCESS")) {
            return "redirect:/?registered";
        }

        model.addAttribute("message", response);
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication,
                            Model model) {

        model.addAttribute("username", authentication.getName());

        model.addAttribute(
                "role",
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );

        return "dashboard";
    }
}