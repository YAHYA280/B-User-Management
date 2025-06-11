package com.marketingconfort.brainboost.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
public class TimeScreenRequest {
    private Duration duration;
    private Long childId;
}
