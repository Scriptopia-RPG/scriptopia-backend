package com.scriptopia.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MainStat {
    @JsonProperty("intelligence")
    INTELLIGENCE,
    @JsonProperty("strength")
    STRENGTH,
    @JsonProperty("agility")
    AGILITY,
    @JsonProperty("luck")
    LUCK
}
