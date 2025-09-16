package com.scriptopia.demo.dto.sharedgame;

import com.scriptopia.demo.domain.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SharedGameRequest {
    private UUID uuid;
}
