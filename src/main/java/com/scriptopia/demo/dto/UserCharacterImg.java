package com.scriptopia.demo.dto;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserCharacterImg {

    @Id
    @GeneratedValue
    private long id;


    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String imgUrl;
}
