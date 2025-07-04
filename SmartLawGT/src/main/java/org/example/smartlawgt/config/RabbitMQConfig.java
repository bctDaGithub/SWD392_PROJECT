package org.example.smartlawgt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // EXCHANGES
    public static final String USAGE_PACKAGE_EXCHANGE = "usagePackageExchange";
    public static final String USER_PACKAGE_EXCHANGE = "userPackageExchange";
    public static final String USER_EXCHANGE = "userExchange";

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

    // QUEUES for user events
    public static final String USER_CREATED_QUEUE = "userCreatedQueue";
    public static final String USER_UPDATED_QUEUE = "userUpdatedQueue";
    public static final String USER_BLOCKED_QUEUE = "userBlockedQueue";
    public static final String USER_UNBLOCKED_QUEUE = "userUnblockedQueue";

    // ROUTING KEYS for user events
    public static final String USER_ROUTING_KEY_CREATED = "user.created";
    public static final String USER_ROUTING_KEY_UPDATED = "user.updated";
    public static final String USER_ROUTING_KEY_BLOCKED = "user.blocked";
    public static final String USER_ROUTING_KEY_UNBLOCKED = "user.unblocked";

    //notification
    public static final String BROADCAST_QUEUE = "notification.broadcast.queue";
    public static final String BROADCAST_KEY = "notification.broadcast";
    public static final String NOTIFICATION_TOGGLED_QUEUE = "notification.toggled.queue";
    public static final String NOTIFICATION_TOGGLED_KEY = "notification.toggled";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(USER_CREATED_QUEUE);
    }

    @Bean
    public Queue userUpdatedQueue() {
        return new Queue(USER_UPDATED_QUEUE);
    }

    @Bean
    public Queue userBlockedQueue() {
        return new Queue(USER_BLOCKED_QUEUE);
    }

    @Bean
    public Queue userUnblockedQueue() {
        return new Queue(USER_UNBLOCKED_QUEUE);
    }

    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(userCreatedQueue())
                .to(userExchange())
                .with(USER_ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding userUpdatedBinding() {
        return BindingBuilder.bind(userUpdatedQueue())
                .to(userExchange())
                .with(USER_ROUTING_KEY_UPDATED);
    }

    @Bean
    public Binding userBlockedBinding() {
        return BindingBuilder.bind(userBlockedQueue())
                .to(userExchange())
                .with(USER_ROUTING_KEY_BLOCKED);
    }

    @Bean
    public Binding userUnblockedBinding() {
        return BindingBuilder.bind(userUnblockedQueue())
                .to(userExchange())
                .with(USER_ROUTING_KEY_UNBLOCKED);
    }

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
    // Exchange
    public static final String LAW_EXCHANGE = "law.exchange";
    public static final String LAW_TYPE_EXCHANGE = "lawtype.exchange";

    // Queue
    public static final String LAW_CREATED_QUEUE = "law.created.queue";
    public static final String LAW_UPDATED_QUEUE = "law.updated.queue";
    public static final String LAW_DELETED_QUEUE = "law.deleted.queue";

    public static final String LAW_TYPE_CREATED_QUEUE = "lawtype.created.queue";
    public static final String LAW_TYPE_UPDATED_QUEUE = "lawtype.updated.queue";
    public static final String LAW_TYPE_DELETED_QUEUE = "lawtype.deleted.queue";

    // Routing Keys
    public static final String LAW_CREATED_KEY = "law.created";
    public static final String LAW_UPDATED_KEY = "law.updated";
    public static final String LAW_DELETED_KEY = "law.deleted";

    public static final String LAW_TYPE_CREATED_KEY = "lawtype.created";
    public static final String LAW_TYPE_UPDATED_KEY = "lawtype.updated";
    public static final String LAW_TYPE_DELETED_KEY = "lawtype.deleted";

    @Bean
    public TopicExchange lawExchange() {
        return new TopicExchange(LAW_EXCHANGE);
    }

    @Bean
    public Queue lawCreatedQueue() {
        return QueueBuilder.durable(LAW_CREATED_QUEUE).build();
    }

    @Bean
    public Queue lawUpdatedQueue() {
        return QueueBuilder.durable(LAW_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue lawDeletedQueue() {
        return QueueBuilder.durable(LAW_DELETED_QUEUE).build();
    }


    @Bean
    public Binding lawCreatedBinding() {
        return BindingBuilder
                .bind(lawCreatedQueue())
                .to(lawExchange())
                .with(LAW_CREATED_KEY);
    }

    @Bean
    public Binding lawUpdatedBinding() {
        return BindingBuilder
                .bind(lawUpdatedQueue())
                .to(lawExchange())
                .with(LAW_UPDATED_KEY);
    }

    @Bean
    public Binding lawDeletedBinding() {
        return BindingBuilder
                .bind(lawDeletedQueue())
                .to(lawExchange())
                .with(LAW_DELETED_KEY);
    }
    // lawtype
    @Bean
    public TopicExchange lawTypeExchange() {
        return new TopicExchange(LAW_TYPE_EXCHANGE);
    }

    @Bean
    public Queue lawTypeCreatedQueue() {
        return QueueBuilder.durable(LAW_TYPE_CREATED_QUEUE).build();
    }

    @Bean
    public Queue lawTypeUpdatedQueue() {
        return QueueBuilder.durable(LAW_TYPE_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue lawTypeDeletedQueue() {
        return QueueBuilder.durable(LAW_TYPE_DELETED_QUEUE).build();
    }

    // LawType Bindings
    @Bean
    public Binding lawTypeCreatedBinding() {
        return BindingBuilder
                .bind(lawTypeCreatedQueue())
                .to(lawTypeExchange())
                .with(LAW_TYPE_CREATED_KEY);
    }

    @Bean
    public Binding lawTypeUpdatedBinding() {
        return BindingBuilder
                .bind(lawTypeUpdatedQueue())
                .to(lawTypeExchange())
                .with(LAW_TYPE_UPDATED_KEY);
    }

    @Bean
    public Binding lawTypeDeletedBinding() {
        return BindingBuilder
                .bind(lawTypeDeletedQueue())
                .to(lawTypeExchange())
                .with(LAW_TYPE_DELETED_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange("notification.exchange");
    }

    // Queues
    @Bean
    public Queue notificationCreatedQueue() {
        return QueueBuilder.durable("notification.created.queue").build();
    }
    @Bean
    public Queue notificationReadQueue() {
        return new Queue("notification.read.queue", true);
    }
    @Bean
    public Queue notificationEmailQueue() {
        return QueueBuilder.durable("notification.email.queue").build();
    }

    @Bean
    public Queue notificationPushQueue() {
        return QueueBuilder.durable("notification.push.queue").build();
    }

    // Bindings
    @Bean
    public Binding notificationCreatedBinding() {
        return BindingBuilder
                .bind(notificationCreatedQueue())
                .to(notificationExchange())
                .with("notification.created");
    }
    @Bean
    public Binding notificationReadBinding() {
        return BindingBuilder
                .bind(notificationReadQueue())
                .to(notificationExchange())
                .with("notification.read");
    }
    @Bean
    public Binding notificationEmailBinding() {
        return BindingBuilder
                .bind(notificationEmailQueue())
                .to(notificationExchange())
                .with("notification.email");
    }

    @Bean
    public Binding notificationPushBinding() {
        return BindingBuilder
                .bind(notificationPushQueue())
                .to(notificationExchange())
                .with("notification.push");
    }
    @Bean
    public Queue broadcastQueue() {
        return QueueBuilder.durable(BROADCAST_QUEUE).build();
    }

    @Bean
    public Binding broadcastBinding() {
        return BindingBuilder
                .bind(broadcastQueue())
                .to(notificationExchange())
                .with(BROADCAST_KEY);
    }
    @Bean
    public Queue notificationToggledQueue() {
        return QueueBuilder.durable(NOTIFICATION_TOGGLED_QUEUE).build();
    }

    // Binding declaration
    @Bean
    public Binding notificationToggledBinding() {
        return BindingBuilder
                .bind(notificationToggledQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_TOGGLED_KEY);
    }




}