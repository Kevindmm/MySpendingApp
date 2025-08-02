package com.kevindmm.spendingapp.schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Currency;

public class CurrencyResponse {

    @JsonProperty
    Currency currency;
    @JsonProperty("default")
    boolean isDefault;

    @JsonProperty
    String symbol;

    public CurrencyResponse(Currency currency, boolean isDefault, String symbol) {
        this.currency = currency;
        this.isDefault = isDefault;
        this.symbol = symbol;
    }
}