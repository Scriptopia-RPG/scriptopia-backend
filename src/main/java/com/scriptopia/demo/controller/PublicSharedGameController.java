package com.scriptopia.demo.controller;

import com.scriptopia.demo.service.SharedGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/games/shared")
@RequiredArgsConstructor
public class PublicSharedGameController {
    private final SharedGameService sharedGameService;

    @GetMapping("/{sharedGameId}")
    public ResponseEntity<?> getSharedGameDetail(@PathVariable Long sharedGameId) {
        return sharedGameService.getDetailedSharedGame(sharedGameId);
    }
}
