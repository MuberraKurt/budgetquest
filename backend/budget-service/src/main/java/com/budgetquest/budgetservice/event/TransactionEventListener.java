package com.budgetquest.budgetservice.event;

import com.budgetquest.budgetservice.config.RabbitMqConfig;
import com.budgetquest.budgetservice.service.BudgetSummaryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {

    private final BudgetSummaryService budgetSummaryService;

    public TransactionEventListener(BudgetSummaryService budgetSummaryService) {
        this.budgetSummaryService = budgetSummaryService;
    }

    @RabbitListener(queues = RabbitMqConfig.TRANSACTION_QUEUE)
    public void handle(TransactionChangedEvent event) {
        budgetSummaryService.apply(event);
    }
}