package com.scriptopia.demo.dto;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Auction {

    @Id @GeneratedValue
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    private UserItem userItem;


    private long price;
    private LocalDateTime createdAt;
    private LocalDateTime tradedAt;

}
