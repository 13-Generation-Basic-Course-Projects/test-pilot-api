package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.model.ExecutionBatch;
import com.both.testing_pilot_backend.model.User;
import com.both.testing_pilot_backend.repository.ExecutionBatchRepository;
import com.both.testing_pilot_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value; // Import @Value
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper; // Import ObjectMapper
import com.fasterxml.jackson.databind.JsonNode; // Import JsonNode

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionServiceNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ExecutionBatchRepository batchRepository;
    private final UserRepository userRepository; // To get user email/details (for routing by email)
    private final ObjectMapper objectMapper; // Inject ObjectMapper

    // Get the queue name from configuration
    @Value("${app.rabbitmq.execution-updates-queue}")
    private String executionUpdatesQueueName;

    /**
     * Listens to RabbitMQ messages from the test execution worker.
     * The worker publishes updates on batch status changes or individual test result completions.
     * @param message A JSON string representing the update.
     */
    @RabbitListener(queues = "${app.rabbitmq.execution-updates-queue}") // Use @Value for queue name
    public void handleExecutionUpdateFromWorker(String message) {
        log.info("Received execution update from RabbitMQ: {}", message);
        try {
            JsonNode updateJson = objectMapper.readTree(message);
            UUID batchId = UUID.fromString(updateJson.get("batchId").asText());

            ExecutionBatch batch = batchRepository.findById(batchId);
            if (batch == null) {
                log.warn("Batch not found for received update, ID: {}", batchId);
                return;
            }

            String recipientEmail = null;
            if (batch.getUserId() != null) {
                User user = userRepository.findById(batch.getUserId());
                if (user != null) {
                    recipientEmail = user.getEmail();
                }
            }

            if (recipientEmail != null) {
                // Send update to user-specific queue: /user/{username}/queue/execution-updates
                messagingTemplate.convertAndSendToUser(
                        recipientEmail,
                        "/queue/execution-updates", // Frontend subscribes to /user/<email>/queue/execution-updates
                        message // The original message (JSON string)
                );
                log.info("Forwarded update for batch {} to user {} via WebSocket.", batchId, recipientEmail);
            } else {
                log.warn("No user found for batch {}, not forwarding update over WebSocket.", batchId);
            }

        } catch (Exception e) {
            log.error("Error processing RabbitMQ execution update message: {}", message, e);
        }
    }
}
