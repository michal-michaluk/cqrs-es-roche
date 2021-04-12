package devices.configuration.device.events;

import lombok.Value;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
public class DeviceStatuses {

    String deviceId;
    List<String> statuses;

    public <T> List<T> map(Function<String, T> mapper) {
        return getStatuses().stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
}
