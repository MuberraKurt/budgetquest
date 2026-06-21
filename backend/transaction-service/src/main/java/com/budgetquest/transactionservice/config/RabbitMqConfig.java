package com.budgetquest.transactionservice.config;

import com.budgetquest.transactionservice.event.TransactionEventPublisher;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    TopicExchange transactionExchange() {
        return new TopicExchange(TransactionEventPublisher.EXCHANGE_NAME);
    }

    @Bean
    JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}