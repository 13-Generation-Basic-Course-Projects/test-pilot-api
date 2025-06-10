package com.both.testing_pilot_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory broker.
        // For production, you'd likely use an external broker relay (e.g., RabbitMQ STOMP plugin, ActiveMQ).
        // If you were using RabbitMQ's STOMP plugin directly, it would look like this:
        /*
        config.enableStompBrokerRelay("/topic", "/queue", "/user")
            .setRelayHost("localhost") // RabbitMQ host
            .setRelayPort(61613)      // STOMP port for RabbitMQ STOMP plugin
            .setClientLogin("guest")
            .setClientPasscode("guest")
            .setSystemLogin("guest")
            .setSystemPasscode("guest");
        */

        // For simplicity with RabbitMQ as internal message queue:
        // We publish to RabbitMQ from the service layer, and then the
        // ExecutionServiceNotificationService uses SimpMessagingTemplate to forward
        // to WebSocket clients. So, the SimpleBroker is sufficient here.
        config.enableSimpleBroker("/topic", "/queue", "/user");


        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
