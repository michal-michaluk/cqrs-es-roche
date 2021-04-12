package devices.configuration.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import devices.configuration.DomainEvent;
import devices.configuration.EventTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class EventOutbox {

    private final Clock clock;
    private final ObjectMapper mapper;
    private final OutboxMessageRepository outboxRepository;
    private final KafkaTemplate<String, String> kafka;
    private final OutboxConfiguration configuration;

    @Value("${outbox.enabled:true}")
    private boolean enabled;

    @Value("${outbox.batch:1000}")
    private int batchSize;

    public EventOutbox(Clock clock, ObjectMapper mapper, OutboxMessageRepository outboxRepository,
                       KafkaTemplate<String, String> kafka, OutboxConfiguration configuration) {
        this.clock = clock;
        this.mapper = mapper;
        this.outboxRepository = outboxRepository;
        this.kafka = kafka;
        this.configuration = configuration;
    }

    @EventListener
    public OutboxMessage store(DomainEvent event) {
        EventTypes.Type type = EventTypes.of(event);
        if (configuration.definedFor(event)) {
            OutboxMessage message = new OutboxMessage(
                    UUID.randomUUID(), Instant.now(clock), type, event
            );
            if (enabled) {
                outboxRepository.save(message);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        send(message);
                    }
                });
            } else {
                PreparedMessage prepared = prepare(message);
                kafka.send(prepared.getTopic(), prepared.getKey(), prepared.getPayload());
            }
            return message;
        }
        return null;
    }

    @Scheduled(fixedDelayString = "${outbox.delay:PT10S}")
    void send() {
        send(outboxRepository.findFirstPage(batchSize));
    }

    private void send(Page<OutboxMessage> messages) {
        for (OutboxMessage message : messages) {
            send(message);
        }
    }

    private void send(OutboxMessage message) {
        PreparedMessage prepared = prepare(message);
        kafka.send(prepared.getTopic(), prepared.getKey(), prepared.getPayload())
                .addCallback(
                        success -> outboxRepository.deleteByIdInSeparateTransaction(message.getEventId()),
                        exception -> log.error("Could not send outbox message topic: {}, eventId: {}, payload: {}",
                                prepared.getTopic(), message.getEventId(), prepared.getPayload(), exception)
                );
    }

    private PreparedMessage prepare(OutboxMessage message) {
        try {
            var config = configuration.ofEvent(message.getPayload());
            return new PreparedMessage(config.getTopic(),
                    config.partitionKey(message.getPayload()),
                    mapper.writeValueAsString(message)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @lombok.Value
    private static class PreparedMessage {
        String topic;
        String key;
        String payload;
    }
}
