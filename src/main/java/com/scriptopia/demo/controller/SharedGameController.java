package com.scriptopia.demo.controller;

import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.service.SharedGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class SharedGameController {
    private final SharedGameService sharedGameService;

    @PostMapping("/share/{id}")
    public ResponseEntity<?> share(@RequestHeader(value = "Authorization")String token,
                                   @PathVariable Long Id) {
        return sharedGameService.saveSharedGame(token, Id);
    }
}
