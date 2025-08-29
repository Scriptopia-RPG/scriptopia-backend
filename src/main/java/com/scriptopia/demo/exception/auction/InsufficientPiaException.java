package com.scriptopia.demo.exception.auction;

public class InsufficientPiaException extends AuctionException {
    public InsufficientPiaException() {
        super("금액이 부족합니다.");
    }
}