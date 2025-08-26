package com.hp.bingo.scheduler;
import java.util.Map;

// MailScheduler.java
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hp.bingo.service.AdminInsightService;
import com.hp.bingo.service.mail.MailTemplates;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailScheduler {
    
    private final MailTemplates mailTemplates;
    private final AdminInsightService adminInsightService;

    // ✅ New scheduled method for admin insight email
    @Scheduled(cron = "0 0 9 * * ?") // Every day at 9 AM
    public void sendAdminInsightEmail() {
        Map<String, Object> insights = adminInsightService.getDailyInsights();
        mailTemplates.sendAdminInsightEmail(insights);
    }
}