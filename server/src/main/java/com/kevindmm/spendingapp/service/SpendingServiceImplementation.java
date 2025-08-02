package com.kevindmm.spendingapp.service;

import com.kevindmm.spendingapp.model.Transaction;
import com.kevindmm.spendingapp.repository.TransactionRepository;
import com.kevindmm.spendingapp.util.DateRange;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class SpendingServiceImplementation implements SpendingService {

  private final TransactionRepository transactionRepository;
  private final DateRange rangeResolver;

  public SpendingServiceImplementation(
    TransactionRepository transactionRepository,
    DateRange rangeResolver
  ) {
    this.transactionRepository = transactionRepository;
    this.rangeResolver = rangeResolver;
  }

  public List<Map<String, Object>> getSpendingData(int range, String frame) {
    String[] dateRange = rangeResolver.resolveDateRange(frame, range);

    List<Transaction> transactions = transactionRepository.getTransactionsForRange(
      dateRange[0],
      dateRange[1]
    );

    Map<String, Float> totalAmountMap = new HashMap<>();

    transactions.forEach(transaction -> {
      LocalDate date = LocalDate.of(
        transaction.getDate().getYear(),
        transaction.getDate().getMonth(),
        transaction.getDate().getDayOfMonth()
      );
      String dateString = date.toString();

      if (totalAmountMap.containsKey(dateString)) {
        totalAmountMap.replace(
          dateString,
          totalAmountMap.get(dateString) + transaction.getAmount()
        );
      } else {
        totalAmountMap.put(dateString, transaction.getAmount());
      }
    });

    List<Map<String, Object>> formattedSpendingData = new ArrayList<>();

    for (String date : totalAmountMap.keySet()) {
      Map<String, Object> tmpMap = new HashMap<>();
      tmpMap.put("totalAmount", totalAmountMap.get(date));
      tmpMap.put("startDate", LocalDate.parse(date).atStartOfDay().toString());
      formattedSpendingData.add(tmpMap);
    }

    formattedSpendingData.sort(
      Comparator.comparing(o -> LocalDateTime.parse((String) o.get("startDate"))
      )
    );

    return formattedSpendingData;
  }
}
