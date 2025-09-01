package com.scriptopia.demo.dto.sharedgame;

import java.util.List;

public record CursorPage<T>(List<T> items, Long nextCursor, boolean hasNext) {}
