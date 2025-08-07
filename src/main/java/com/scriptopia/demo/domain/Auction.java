package com.scriptopia.demo.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Auction {

    @Id @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private UserItem userItem;


    private Long price;
    private LocalDateTime createdAt;
    private LocalDateTime tradedAt;

}
