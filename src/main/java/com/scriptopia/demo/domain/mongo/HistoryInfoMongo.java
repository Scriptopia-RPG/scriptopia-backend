package com.scriptopia.demo.domain.mongo;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryInfoMongo {
    private String title;
    private String worldView;
    private String backgroundStory;
    private String worldPrompt;
    private String epilogue1Title;
    private String epilogue1Content;
    private String epilogue2Title;
    private String epilogue2Content;
    private String epilogue3Title;
    private String epilogue3Content;
    private Integer score;
}
