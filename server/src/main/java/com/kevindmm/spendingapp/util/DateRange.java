package com.kevindmm.spendingapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DateRange {

    @Autowired
    private Clock clock;

    public static final String DAILY = "daily";
    public static final String MONTHLY = "monthly";
    public static final String YEARLY = "yearly";

    public String[] resolveDateRange(String frame, int range) {
        LocalDate from = LocalDate.now(clock);
        LocalDate to = LocalDate.now(clock);


        // subtract one from the range as today is included
        range = range - 1;
        to = to.plusDays(1);

        switch (frame) {
            case DAILY -> {
                from = from.minusDays(range);
            }
            case MONTHLY -> {
                from = from.minusMonths(range).withDayOfMonth(1);
            }
            case YEARLY -> {
                from = from.minusYears(range).withDayOfYear(1);
            }
            default -> {
            }
        }

        return new String[]{from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))};
    }
}