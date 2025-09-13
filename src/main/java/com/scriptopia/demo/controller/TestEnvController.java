package com.scriptopia.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class TestEnvController {
    @GetMapping("/")
    public String mainPage() {
        return "index"; // templates/index.html
    }
}
