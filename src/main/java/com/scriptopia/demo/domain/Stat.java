package com.scriptopia.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Stat {
    @JsonProperty("intelligence")
    INTELLIGENCE,
    @JsonProperty("strength")
    STRENGTH,
    @JsonProperty("agility")
    AGILITY,
    @JsonProperty("luck")
    LUCK
}
