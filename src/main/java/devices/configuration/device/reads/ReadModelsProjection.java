package devices.configuration.device.reads;

import devices.configuration.device.DeviceSnapshot;
import devices.configuration.device.events.DeviceStatuses;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReadModelsProjection {

    private final DeviceReadsRepository repository;

    @EventListener
    public void handle(DeviceSnapshot details) {
        DeviceReadsEntity entity = repository.findById(details.getDeviceId())
                .orElseGet(() -> new DeviceReadsEntity(details.getDeviceId()));

        entity
                .setOwnership(details.getOwnership())
                .setDetails(details)
                .setPin(DevicePin.ofNullable(details, entity.getStatuses()))
                .setSummary(DeviceSummary.ofNullable(details, entity.getStatuses()));

        repository.save(entity);
    }

    @EventListener
    public void handle(DeviceStatuses statuses) {
        DeviceReadsEntity entity = repository.findById(statuses.getDeviceId())
                .orElseGet(() -> new DeviceReadsEntity(statuses.getDeviceId()));

        entity
                .setStatuses(statuses)
                .setPin(DevicePin.ofNullable(entity.getDetails(), statuses))
                .setSummary(DeviceSummary.ofNullable(entity.getDetails(), statuses));

        repository.save(entity);
    }
}
