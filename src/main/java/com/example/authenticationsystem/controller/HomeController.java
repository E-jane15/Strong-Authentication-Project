package com.example.authenticationsystem.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.example.authenticationsystem.entity.User;
import com.example.authenticationsystem.service.EmailService;
import com.example.authenticationsystem.service.OtpService;
import com.example.authenticationsystem.service.QrCodeService;
import com.example.authenticationsystem.service.TotpService;
import com.example.authenticationsystem.service.TwoFactorService;
import com.example.authenticationsystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final UserService service;
    private final EmailService emailService;
    private final OtpService otpService;
    private final QrCodeService qrCodeService;
    private final TotpService totpService;
    private final TwoFactorService twoFactorService;

    public HomeController(UserService service,
                          EmailService emailService,
                          OtpService otpService,
                          QrCodeService qrCodeService,
                          TotpService totpService,
                          TwoFactorService twoFactorService) {

        this.service = service;
        this.emailService = emailService;
        this.otpService = otpService;
        this.qrCodeService = qrCodeService;
        this.totpService = totpService;
        this.twoFactorService = twoFactorService;
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

        if (!response.equals("SUCCESS")) {
            model.addAttribute("message", response);
            return "register";
        }

        // Send Email OTP
        String otp = otpService.generateOtp(user.getEmail());
        emailService.sendOtp(user.getEmail(), otp);

        // Get saved user
        User savedUser = service.findByUsername(user.getUsername());

        // Generate QR Code
        String qrCode = qrCodeService.generateQrCode(
                savedUser.getUsername(),
                savedUser.getTotpSecret()
        );

        model.addAttribute("qrCode", qrCode);
        model.addAttribute("username", savedUser.getUsername());

        return "setup-authenticator";
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage() {
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            Model model) {

        if (otpService.verifyOtp(email, otp)) {
            model.addAttribute("message",
                    "Email verified successfully!");
            return "login";
        }

        model.addAttribute("message", "Invalid OTP.");
        return "verify-otp";
    }

    @GetMapping("/verify-totp")
    public String verifyTotpPage(Authentication authentication,
                                 Model model) {

        model.addAttribute("username", authentication.getName());
        return "verify-totp";
    }

    @PostMapping("/verify-totp")
    public String verifyTotp(Authentication authentication,
                             @RequestParam String code,
                             Model model) {

        String username = authentication.getName();

        User user = service.findByUsername(username);

        if (user == null) {
            model.addAttribute("message", "User not found");
            model.addAttribute("username", username);
            return "verify-totp";
        }

        boolean valid = totpService.verifyCode(
                user.getTotpSecret(),
                code
        );

        if (valid) {
            twoFactorService.markVerified(username);
            return "redirect:/dashboard";
        }

        model.addAttribute("username", username);
        model.addAttribute("message", "Invalid Google Authenticator code.");

        return "verify-totp";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication,
                            Model model) {

        String username;

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {

            OAuth2User oauthUser = oauthToken.getPrincipal();

            username = oauthUser.getAttribute("preferred_username");

            if (username == null) {
                username = oauthUser.getAttribute("email");
            }

            if (username == null) {
                username = oauthUser.getName();
            }

        } else {

            username = authentication.getName();

            if (!twoFactorService.isVerified(username)) {
                return "redirect:/verify-totp";
            }
        }

        model.addAttribute("username", username);

        model.addAttribute(
                "role",
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );

        return "dashboard";
    }
    @GetMapping("/setup-authenticator")
    public String setupAuthenticatorPage() {
        return "setup-authenticator";
    }
}