package devices.configuration.device.reads;

import devices.configuration.device.DeviceSnapshot;
import devices.configuration.device.Location;
import devices.configuration.device.events.DeviceStatuses;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
public class DeviceSummary {
    String deviceId;
    Location location;
    List<String> statuses;

    public static DeviceSummary ofNullable(DeviceSnapshot details, DeviceStatuses statuses) {
        if (details == null || details.getLocation() == null || details.getLocation().getCoordinates() == null) {
            return null;
        }
        return new DeviceSummary(
                details.getDeviceId(),
                details.getLocation(),
                Optional.ofNullable(statuses)
                        .map(DeviceStatuses::getStatuses)
                        .orElse(List.of("Faulted")));
    }
}
