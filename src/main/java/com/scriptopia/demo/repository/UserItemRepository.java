package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.ItemDef;
import com.scriptopia.demo.domain.TradeStatus;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserItem;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    Optional<UserItem> findByItemDefAndTradeStatus(ItemDef itemDef, TradeStatus tradeStatus);

    Optional<UserItem> findByUserIdAndItemId(Long userId, Long itemId);
}
