package com.example.authenticationsystem.controller;

import com.example.authenticationsystem.entity.User;
import com.example.authenticationsystem.service.EmailService;
import com.example.authenticationsystem.service.OtpService;
import com.example.authenticationsystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    private final UserService service;
    private final EmailService emailService;
    private final OtpService otpService;

    public HomeController(UserService service,
                          EmailService emailService,
                          OtpService otpService) {

        this.service = service;
        this.emailService = emailService;
        this.otpService = otpService;
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

            String otp = otpService.generateOtp(user.getEmail());

            emailService.sendOtp(user.getEmail(), otp);

            return "redirect:/verify-otp";
        }

        model.addAttribute("message", response);
        return "register";
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage() {
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(String email,
                            String otp,
                            Model model) {

        if (otpService.verifyOtp(email, otp)) {

            model.addAttribute("message",
                    "Email verified successfully!");

            return "login";
        }

        model.addAttribute("message",
                "Invalid OTP.");

        return "verify-otp";
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