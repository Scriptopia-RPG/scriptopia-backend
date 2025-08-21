package com.scriptopia.demo.repository;


import com.scriptopia.demo.record.RefreshSession;

import java.util.Optional;

public interface RefreshRepository {
    void save(RefreshSession session);
    Optional<RefreshSession> find(long userId, String jti);
    void delete(long userId, String jti);

    Optional<RefreshSession> findByDevice(long userId, String device);
    void deleteByDevice(long userId, String device);

    void deleteAllForUser(long userId);
}
