package com.kevindmm.spendingapp.service;

import java.util.List;
import java.util.Map;

public interface SpendingService {
  public List<Map<String, Object>> getSpendingData(int range, String frame);
}
