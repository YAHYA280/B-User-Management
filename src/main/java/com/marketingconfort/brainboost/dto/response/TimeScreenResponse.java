package com.marketingconfort.brainboost.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
@Getter
@Setter
public class TimeScreenResponse {
    private Long id;
    private Duration duration;
    private LocalDateTime createdAt;
    private Long childId;
}
