package com.scriptopia.demo.refresh;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record RefreshSession(
        Long userId,
        String jti,
        String tokenHash, // refresh 원문 해시
        String deviceId,
        long expEpochSec,
        long createdEpochSec,
        String ip,
        String ua
) {

}
