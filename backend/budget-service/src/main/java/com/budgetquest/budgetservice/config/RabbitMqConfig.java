package com.budgetquest.budgetservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String TRANSACTION_EXCHANGE = "budgetquest.transactions";
    public static final String TRANSACTION_QUEUE = "budget-service.transaction-events";
    public static final String TRANSACTION_ROUTING_PATTERN = "transaction.*";

    @Bean
    TopicExchange transactionExchange() {
        return new TopicExchange(TRANSACTION_EXCHANGE);
    }

    @Bean
    Queue transactionQueue() {
        return new Queue(TRANSACTION_QUEUE, true);
    }

    @Bean
    Binding transactionBinding(
            Queue transactionQueue,
            TopicExchange transactionExchange
    ) {
        return BindingBuilder
                .bind(transactionQueue)
                .to(transactionExchange)
                .with(TRANSACTION_ROUTING_PATTERN);
    }

    @Bean
    JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}