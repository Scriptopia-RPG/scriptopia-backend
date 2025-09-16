package com.scriptopia.demo.domain.mongo;

import com.scriptopia.demo.domain.ChoiceEventType;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceInfoMongo {
    private ChoiceEventType eventType; // living, nonliving
    private String story;
    private List<ChoiceMongo> choice;
}
