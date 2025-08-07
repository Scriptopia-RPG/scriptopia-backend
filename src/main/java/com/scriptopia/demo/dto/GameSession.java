package com.scriptopia.demo.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class GameSession {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    // 아직 mongoDB와 연동되는 지 모름 TYPE도 String(uuid)인지 LONG으로 할건지
    private long mongoId;

}
