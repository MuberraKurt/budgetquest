package com.budgetquest.transactionservice.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventPublisher {

    public static final String EXCHANGE_NAME = "budgetquest.transactions";
    private static final String ROUTING_KEY_PREFIX = "transaction.";

    private final RabbitTemplate rabbitTemplate;

    public TransactionEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(TransactionChangedEvent event) {
        String routingKey = ROUTING_KEY_PREFIX + event.action().toLowerCase();
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, event);
    }
}