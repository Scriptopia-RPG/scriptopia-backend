package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.Auction;
import com.scriptopia.demo.domain.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    boolean existsByUserItem(UserItem userItem);
}
