package devices.configuration.device.reads;

import devices.configuration.device.DeviceSnapshot;
import devices.configuration.device.Location;
import devices.configuration.device.events.DeviceStatuses;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
public class DevicePin {

    enum Status {AVAILABLE, CHARGING, FAULTED}

    String deviceId;
    Location.Coordinates coordinates;
    List<Status> statuses;

    public static DevicePin ofNullable(DeviceSnapshot details, DeviceStatuses statuses) {
        if (details == null || details.getLocation() == null || details.getLocation().getCoordinates() == null) {
            return null;
        }
        return new DevicePin(
                details.getDeviceId(),
                details.getLocation().getCoordinates(),
                Optional.ofNullable(statuses)
                        .map(s -> s.map(DevicePin::normalised))
                        .orElse(List.of(Status.FAULTED))
        );
    }

    private static Status normalised(String raw) {
        switch (raw) {
            case "Available":
                return Status.AVAILABLE;
            case "Faulted":
                return Status.FAULTED;
            default:
                return Status.CHARGING;
        }
    }
}
