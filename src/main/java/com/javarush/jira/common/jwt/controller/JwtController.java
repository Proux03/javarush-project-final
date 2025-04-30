package com.javarush.jira.common.jwt.controller;

import com.javarush.jira.common.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(JwtController.REST_URL)
@RequiredArgsConstructor
public class JwtController {
    public static final String REST_URL = "/jwt/token";

    private final JwtService jwtService;

    @GetMapping
    public String generateToken(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("jwt", jwtService.generateToken(userDetails));
        return "index";
    }
}