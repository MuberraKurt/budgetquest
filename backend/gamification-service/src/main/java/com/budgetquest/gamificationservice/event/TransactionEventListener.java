package com.budgetquest.gamificationservice.event;

import com.budgetquest.gamificationservice.config.RabbitMqConfig;
import com.budgetquest.gamificationservice.service.GamificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventListener {

    private final GamificationService gamificationService;

    public TransactionEventListener(GamificationService gamificationService) {
        this.gamificationService = gamificationService;
    }

    @RabbitListener(queues = RabbitMqConfig.TRANSACTION_QUEUE)
    public void handle(TransactionChangedEvent event) {
        gamificationService.handleTransactionEvent(event);
    }
}