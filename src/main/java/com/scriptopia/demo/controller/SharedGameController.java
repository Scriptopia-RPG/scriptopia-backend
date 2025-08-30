package com.scriptopia.demo.controller;

import com.scriptopia.demo.service.SharedGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class SharedGameController {
    private final SharedGameService sharedGameService;

    @PostMapping("/share/{hid}")
    public ResponseEntity<?> share(Authentication authentication, @PathVariable Long hid) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.saveSharedGame(userId, hid);
    }

    @GetMapping("/games/shared")
    public ResponseEntity<?> getMySharedGames(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.getMySharedGames(userId);
    }

    @DeleteMapping("/share/{gameid}")
    public void delete(Authentication authentication, @PathVariable Long gameid) {
        Long userId = Long.valueOf(authentication.getName());

        sharedGameService.deletesharedGame(userId, gameid);
    }
}
