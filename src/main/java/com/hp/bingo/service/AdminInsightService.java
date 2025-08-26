package com.hp.bingo.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hp.bingo.repo.EntryFormRepository;

import lombok.RequiredArgsConstructor;

// AdminInsightService.java
@Service
@RequiredArgsConstructor
public class AdminInsightService {
    
    private final EntryFormRepository entryFormRepository;
    
    public Map<String, Object> getDailyInsights() {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterdayStart = today.minusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime yesterdayEnd = today.minusDays(1).withHour(23).withMinute(59).withSecond(59);
        
        // Get yesterday's stats
        Map<String, Long> dailyStats = entryFormRepository.getDailyStats(yesterdayStart, yesterdayEnd);
        
        // Get overall stats
        long totalRegistrations = entryFormRepository.getTotalRegistrations();
        long paymentConfirmed = entryFormRepository.getPaymentConfirmedCount();
        long paymentPending = entryFormRepository.getPaymentPendingCount();
        long totalAmount = entryFormRepository.getTotalConfirmedAmount();
        
        Map<String, Object> insights = new HashMap<>();
        insights.put("date", yesterdayStart.toLocalDate().toString());
        insights.put("dailyRegistrations", dailyStats.getOrDefault("totalRegistrations", 0L));
        insights.put("dailyConfirmed", dailyStats.getOrDefault("confirmedPayments", 0L));
        insights.put("dailyPending", dailyStats.getOrDefault("pendingPayments", 0L));
        insights.put("dailyAmount", dailyStats.getOrDefault("totalAmount", 0L));
        insights.put("totalRegistrations", totalRegistrations);
        insights.put("totalConfirmed", paymentConfirmed);
        insights.put("totalPending", paymentPending);
        insights.put("totalAmount", totalAmount);
        
        return insights;
    }
}