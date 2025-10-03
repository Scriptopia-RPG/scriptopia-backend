package com.scriptopia.demo.dto.sharedgame;

import java.util.List;
import java.util.UUID;

public record CursorPage<T>(List<T> items, UUID lastUuid, boolean hasNextPage) {}