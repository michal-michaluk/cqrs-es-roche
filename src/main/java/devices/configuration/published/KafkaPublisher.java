package devices.configuration.published;

import devices.configuration.device.DeviceSnapshot;
import devices.configuration.outbox.EventOutbox;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaPublisher {

    private final EventOutbox outbox;

    @EventListener
    public void handle(DeviceSnapshot details) {
        outbox.store(DeviceSnapshotV1.from(details));
    }
}
