package com.alzheimer.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class DashboardController {

    @GetMapping
    public String index() {
        return "redirect:/admin-dashboard.html";
    }

    @GetMapping("dashboard")
    public String dashboard() {
        return "redirect:/admin-dashboard.html";
    }

    @GetMapping("admin")
    public String admin() {
        return "redirect:/admin-dashboard.html";
    }
}
