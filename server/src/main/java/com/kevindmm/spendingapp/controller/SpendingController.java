package com.kevindmm.spendingapp.controller;

import com.kevindmm.spendingapp.service.SpendingService;
import com.kevindmm.spendingapp.util.DateRange;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Validated
@RequestMapping("/api/spendings")
public class SpendingController {

    private final SpendingService spendingService;

    public SpendingController(SpendingService spendingService) {
        this.spendingService = spendingService;
    }


    @GetMapping("")
    public ResponseEntity<Object> index(
            @RequestParam(name = "range", required = false) Integer range,
            @RequestParam(name = "frame", required = false) String frame
    ) {
        range = range == null ? 6 : range;
        frame = frame == null ? DateRange.DAILY : frame;

        List<Map<String, Object>> formattedSpendingData = this.spendingService.getSpendingData(range, frame);
        Map<String, Object> data = new HashMap<>();
        data.put("spendings", formattedSpendingData);

        return ResponseEntity.ok(data);
    }
}
