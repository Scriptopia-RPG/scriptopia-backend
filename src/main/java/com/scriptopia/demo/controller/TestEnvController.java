package com.scriptopia.demo.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@Tag(name = "백엔드 정적 페이지 관련 API", description = "백엔드 정적 페이지 관련 API 입니다.")
public class TestEnvController {
    @GetMapping("/")
    public String mainPage() {
        return "index"; // templates/index.html
    }

}
