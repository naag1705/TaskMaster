package com.lamar.gradproject.taskmaster.controller;

    import com.lamar.gradproject.taskmaster.model.User;
import com.lamar.gradproject.taskmaster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        // Check if user already exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email already registered");
            return "register";
        }

        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRegisteredOn(LocalDateTime.now());

        userRepository.save(user);

        // Redirect to login with success message
        model.addAttribute("success", "Registration successful. Please login.");
        return "login";
    }
}
