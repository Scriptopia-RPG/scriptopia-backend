package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.ChoiceEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceInfoMongo {
    private ChoiceEventType eventType; // living, nonliving
    private String story;
    private List<ChoiceMongo> choice;
}
