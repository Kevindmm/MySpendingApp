package com.kevindmm.spendingapp.controller;

import com.kevindmm.spendingapp.schema.CurrencyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    @GetMapping
    public ResponseEntity<List<CurrencyResponse>> getCurrencies() {
        List<CurrencyResponse> currencies = new ArrayList<>();
        currencies.add(new CurrencyResponse(Currency.getInstance("USD"), false, "$"));
        currencies.add(new CurrencyResponse(Currency.getInstance("CAD"), true, "$"));
        currencies.add(new CurrencyResponse(Currency.getInstance("EUR"), false, "€"));

        return ResponseEntity.ok(currencies);
    }
}
