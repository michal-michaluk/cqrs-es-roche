package devices.configuration.device.events;

import devices.configuration.DomainEvent;
import devices.configuration.LegacyDomainEvent;
import devices.configuration.device.Ownership;
import lombok.Value;

public class LegacyEvents {

    @Value
    public static class OwnershipUpdatedV1 implements LegacyDomainEvent {
        String deviceId;
        String operator;
        String provider;

        @Override
        public DomainEvent normalise() {
            return new OwnershipUpdated(deviceId,
                    new Ownership(operator, provider)
            );
        }
    }

}
