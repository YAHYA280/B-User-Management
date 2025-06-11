package com.marketingconfort.brainboost.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SuspensionRequest {
    private String reason;
    private LocalDate suspensionEnd;
}
