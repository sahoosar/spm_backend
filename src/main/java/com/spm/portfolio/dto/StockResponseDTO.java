package com.spm.portfolio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockResponseDTO {
    @JsonProperty("Global Quote")
    private GlobalQuoteDTO globalQuote;
}
