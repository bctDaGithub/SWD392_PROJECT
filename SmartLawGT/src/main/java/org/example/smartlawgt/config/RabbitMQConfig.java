package org.example.smartlawgt.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // EXCHANGES
    public static final String USAGE_PACKAGE_EXCHANGE = "usagePackageExchange";
    public static final String USER_PACKAGE_EXCHANGE = "userPackageExchange";

    // QUEUES for usage package events
    public static final String USAGE_PACKAGE_CREATED_QUEUE = "usagePackageCreatedQueue";
    public static final String USAGE_PACKAGE_UPDATED_QUEUE = "usagePackageUpdatedQueue";
    public static final String USAGE_PACKAGE_DISABLED_QUEUE = "usagePackageDisabledQueue";
    public static final String USAGE_PACKAGE_ENABLED_QUEUE = "usagePackageEnabledQueue";

    // ROUTING KEYS for usage package events
    public static final String USAGE_PACKAGE_ROUTING_KEY_CREATED = "usagePackage.created";
    public static final String USAGE_PACKAGE_ROUTING_KEY_UPDATED = "usagePackage.updated";
    public static final String USAGE_PACKAGE_ROUTING_KEY_DISABLED = "usagePackage.disabled";
    public static final String USAGE_PACKAGE_ROUTING_KEY_ENABLED = "usagePackage.enabled";

    // QUEUES for user package events
    public static final String USER_PACKAGE_CREATED_QUEUE = "userPackageCreatedQueue";
    public static final String USER_PACKAGE_UPDATED_QUEUE = "userPackageUpdatedQueue";
    public static final String USER_PACKAGE_EXPIRED_QUEUE = "userPackageExpiredQueue";
    public static final String USER_PACKAGE_STATUS_QUEUE = "userPackageStatusQueue";


    // ROUTING KEYS for user package events
    public static final String USER_PACKAGE_ROUTING_KEY_CREATED = "userPackage.created";
    public static final String USER_PACKAGE_ROUTING_KEY_UPDATED = "userPackage.updated";
    public static final String USER_PACKAGE_ROUTING_KEY_EXPIRED = "userPackage.expired";
    public static final String USER_PACKAGE_ROUTING_KEY_STATUS = "userPackage.status";

    @Bean
    public Queue userPackageStatusQueue() {
        return new Queue(USER_PACKAGE_STATUS_QUEUE, false);
    }

    @Bean
    public Binding userPackageStatusBinding() {
        return BindingBuilder.bind(userPackageStatusQueue())
                .to(userPackageExchange())
                .with(USER_PACKAGE_ROUTING_KEY_STATUS);
    }

    @Bean
    public TopicExchange usagePackageExchange() {
        return new TopicExchange(USAGE_PACKAGE_EXCHANGE);
    }

    @Bean
    public TopicExchange userPackageExchange() {
        return new TopicExchange(USER_PACKAGE_EXCHANGE);
    }

    // Usage Package Queues
    @Bean
    public Queue usagePackageCreatedQueue() {
        return new Queue(USAGE_PACKAGE_CREATED_QUEUE, false);
    }

    @Bean
    public Queue usagePackageUpdatedQueue() {
        return new Queue(USAGE_PACKAGE_UPDATED_QUEUE, false);
    }

    @Bean
    public Queue usagePackageDisabledQueue() {
        return new Queue(USAGE_PACKAGE_DISABLED_QUEUE, false);
    }

    @Bean
    public Queue usagePackageEnabledQueue() {
        return new Queue(USAGE_PACKAGE_ENABLED_QUEUE, false);
    }

    // User Package Queues
    @Bean
    public Queue userPackageCreatedQueue() {
        return new Queue(USER_PACKAGE_CREATED_QUEUE, false);
    }

    @Bean
    public Queue userPackageUpdatedQueue() {
        return new Queue(USER_PACKAGE_UPDATED_QUEUE, false);
    }

    @Bean
    public Queue userPackageExpiredQueue() {
        return new Queue(USER_PACKAGE_EXPIRED_QUEUE, false);
    }

    // Usage Package Bindings
    @Bean
    public Binding usagePackageCreatedBinding() {
        return BindingBuilder.bind(usagePackageCreatedQueue())
                .to(usagePackageExchange())
                .with(USAGE_PACKAGE_ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding usagePackageUpdatedBinding() {
        return BindingBuilder.bind(usagePackageUpdatedQueue())
                .to(usagePackageExchange())
                .with(USAGE_PACKAGE_ROUTING_KEY_UPDATED);
    }

    @Bean
    public Binding usagePackageDisabledBinding() {
        return BindingBuilder.bind(usagePackageDisabledQueue())
                .to(usagePackageExchange())
                .with(USAGE_PACKAGE_ROUTING_KEY_DISABLED);
    }

    @Bean
    public Binding usagePackageEnabledBinding() {
        return BindingBuilder.bind(usagePackageEnabledQueue())
                .to(usagePackageExchange())
                .with(USAGE_PACKAGE_ROUTING_KEY_ENABLED);
    }

    // User Package Bindings
    @Bean
    public Binding userPackageCreatedBinding() {
        return BindingBuilder.bind(userPackageCreatedQueue())
                .to(userPackageExchange())
                .with(USER_PACKAGE_ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding userPackageUpdatedBinding() {
        return BindingBuilder.bind(userPackageUpdatedQueue())
                .to(userPackageExchange())
                .with(USER_PACKAGE_ROUTING_KEY_UPDATED);
    }

    @Bean
    public Binding userPackageExpiredBinding() {
        return BindingBuilder.bind(userPackageExpiredQueue())
                .to(userPackageExchange())
                .with(USER_PACKAGE_ROUTING_KEY_EXPIRED);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }
}