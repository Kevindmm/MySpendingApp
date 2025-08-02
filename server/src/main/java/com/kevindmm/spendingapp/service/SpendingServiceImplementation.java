package com.kevindmm.spendingapp.service;

import com.kevindmm.spendingapp.model.Transaction;
import com.kevindmm.spendingapp.repository.TransactionRepository;
import com.kevindmm.spendingapp.util.DateRange;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class SpendingServiceImplementation implements SpendingService {

  private final TransactionRepository transactionRepository;
  private final DateRange rangeResolver;

  public static final String DAILY = "daily";
  public static final String MONTHLY = "monthly";

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

    Map<LocalDate, Float> totalAmountMap = new HashMap<>();

    transactions.forEach(transaction -> {
      LocalDate dateKey;

      if (DAILY.equalsIgnoreCase(frame)) {
        dateKey = transaction.getDate();
      } else if (MONTHLY.equalsIgnoreCase(frame)) {
        dateKey = transaction.getDate().withDayOfMonth(1);
      } else {
        dateKey = transaction.getDate().withDayOfYear(1);
      }

      if (totalAmountMap.containsKey(dateKey)) {
        totalAmountMap.put(dateKey, totalAmountMap.get(dateKey) + transaction.getAmount());
      } else {
        totalAmountMap.put(dateKey, transaction.getAmount());
      }
    });

    List<Map<String, Object>> formattedSpendingData = new ArrayList<>();

    for (LocalDate date : totalAmountMap.keySet()) {
      Map<String, Object> tmpMap = new HashMap<>();
      tmpMap.put("totalAmount", totalAmountMap.get(date));
      tmpMap.put("startDate", date.atStartOfDay().toString());
      formattedSpendingData.add(tmpMap);
    }

/*    formattedSpendingData.sort(
      Comparator.comparing(o -> LocalDateTime.parse((String) o.get("startDate"))
      )
    );*/

    return formattedSpendingData;
  }
}
