package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.ChoiceEventType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChoiceInfoMongo {
    private ChoiceEventType eventType; // living, nonliving
    private String story;
    private List<ChoiceMongo> choice;
}
