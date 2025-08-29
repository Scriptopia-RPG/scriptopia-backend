package com.scriptopia.demo.exception.auction;

public class AuctionNotFoundException extends AuctionException {
  public AuctionNotFoundException() {
    super("해당 경매가 존재하지 않습니다.");
  }
}
