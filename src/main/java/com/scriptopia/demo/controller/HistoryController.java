package com.scriptopia.demo.controller;

import com.scriptopia.demo.repository.UserRepository;
import com.scriptopia.demo.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

//    @PostMapping("/{id}/history")
//    public ResponseEntity addHistory(@PathVariable Integer id, ) {
//    }
}
