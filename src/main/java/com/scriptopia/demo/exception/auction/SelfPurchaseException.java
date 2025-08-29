package com.scriptopia.demo.exception.auction;

public class SelfPurchaseException extends AuctionException {
  public SelfPurchaseException() {
    super("자기 물건은 구매할 수 없습니다.");
  }
}
