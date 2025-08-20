package com.scriptopia.demo.repository;

import com.scriptopia.demo.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

}
