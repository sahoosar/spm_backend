package com.spm.portfolio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalQuoteDTO {

    @JsonProperty("01. symbol")
    private String stockSymbol;

    @JsonProperty("02. open")
    private String openPrice;

    @JsonProperty("03. high")
    private String highPrice;

    @JsonProperty("04. low")
    private String lowPrice;

    @JsonProperty("05. price")
    private String latestPrice;

    @JsonProperty("06. volume")
    private String volume;

    @JsonProperty("07. latest trading day")
    private String latestTradingDay;

    @JsonProperty("08. previous close")
    private String previousClose;

    @JsonProperty("09. change")
    private String change;

    @JsonProperty("10. change percent")
    private String changePercent;
}
