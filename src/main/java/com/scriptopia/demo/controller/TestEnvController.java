package com.scriptopia.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class TestEnvController {
    @GetMapping("/")
    public String mainPage() {
        return "index"; // templates/index.html
    }

}
