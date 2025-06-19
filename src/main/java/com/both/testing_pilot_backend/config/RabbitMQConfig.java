package com.both.testing_pilot_backend.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Define the queue name as a public static final constant
    // This allows other services to reference it directly for publishing/listening
    public static final String EXECUTION_UPDATES_QUEUE = "execution_updates_queue"; // Directly define the constant here

    @Value("${app.rabbitmq.execution-updates-queue}") // Inject from application.properties
    private String executionUpdatesQueueName; // This field holds the value from properties

    /**
     * Declares the RabbitMQ Queue bean. Spring AMQP will ensure this queue
     * exists on the RabbitMQ broker when the application starts.
     * @return The Queue instance.
     */
    @Bean
    public Queue executionUpdatesQueue() {
        // Use the injected value to create the Queue bean
        return new Queue(executionUpdatesQueueName, true, false, false); // Durable queue
    }
}
