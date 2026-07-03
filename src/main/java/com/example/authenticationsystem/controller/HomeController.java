package com.example.authenticationsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        if (
                username.equals("admin") &&
                        password.equals("admin123")
        ) {

            model.addAttribute("username", "Administrator");

            return "dashboard";
        }

        if (
                username.equals("john") &&
                        password.equals("john123")
        ) {

            model.addAttribute("username", "John");

            return "dashboard";
        }

        if (
                username.equals("mary") &&
                        password.equals("mary123")
        ) {

            model.addAttribute("username", "Mary");

            return "dashboard";
        }

        model.addAttribute(
                "message",
                "Invalid Username or Password"
        );

        return "login";
    }

}
