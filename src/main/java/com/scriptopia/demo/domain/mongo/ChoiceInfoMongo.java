package com.scriptopia.demo.domain.mongo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChoiceInfoMongo {
    private String eventType; // living, nonliving
    private String story;
    private List<ChoiceMongo> choice;
}
