package devices.configuration.published;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import devices.configuration.DomainEvent;
import devices.configuration.device.*;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalTime;

@Value
@Builder
public class DeviceSnapshotV1 implements DomainEvent {

    String deviceId;
    OwnershipSnapshot ownership;
    LocationSnapshot location;
    OpeningHoursSnapshot openingHours;
    VisibilitySnapshot visibility;

    public static DeviceSnapshotV1 from(DeviceSnapshot device) {
        return DeviceSnapshotV1.builder()
                .deviceId(device.getDeviceId())
                .ownership(OwnershipSnapshot.from(device.getOwnership()))
                .location(LocationSnapshot.from(device.getLocation()))
                .openingHours(OpeningHoursSnapshot.from(device.getOpeningHours()))
                .visibility(VisibilitySnapshot.from(device.getVisibility()))
                .build();
    }

    @Value
    @Builder
    static class OwnershipSnapshot {
        String operator;
        String provider;

        public static OwnershipSnapshot from(Ownership ownership) {
            return OwnershipSnapshot.builder()
                    .operator(ownership.getOperator())
                    .provider(ownership.getProvider())
                    .build();
        }
    }

    @Value
    @Builder
    static class LocationSnapshot {
        String street;
        String houseNumber;
        String city;
        String postalCode;
        String state;
        String country;
        CoordinatesSnapshot coordinates;

        public static LocationSnapshot from(Location location) {
            return LocationSnapshot.builder()
                    .street(location.getStreet())
                    .houseNumber(location.getHouseNumber())
                    .city(location.getCity())
                    .postalCode(location.getPostalCode())
                    .state(location.getState())
                    .country(location.getCountry())
                    .coordinates(CoordinatesSnapshot.builder()
                            .latitude(location.getCoordinates().getLatitude())
                            .longitude(location.getCoordinates().getLongitude())
                            .build())
                    .build();
        }

        @Value
        @Builder
        static class CoordinatesSnapshot {
            BigDecimal longitude;
            BigDecimal latitude;
        }
    }

    @Value
    @Builder
    static class VisibilitySnapshot {
        boolean roamingEnabled;
        ForCustomerSnapshot forCustomer;

        public static VisibilitySnapshot from(Visibility visibility) {
            return VisibilitySnapshot.builder().build();
        }

        public enum ForCustomerSnapshot {
            USABLE_AND_VISIBLE_ON_MAP,
            USABLE_BUT_HIDDEN_ON_MAP,
            INACCESSIBLE_AND_HIDDEN_ON_MAP;
        }
    }

    @Value
    @Builder
    static class OpeningHoursSnapshot {

        boolean alwaysOpen;
        WeekSnapshot opened;

        public static OpeningHoursSnapshot from(OpeningHours openingHours) {
            return OpeningHoursSnapshot.builder()
                    // TODO
                    .build();
        }

        @Value
        @Builder
        public static class WeekSnapshot {
            OpeningTimeSnapshot monday;
            OpeningTimeSnapshot tuesday;
            OpeningTimeSnapshot wednesday;
            OpeningTimeSnapshot thursday;
            OpeningTimeSnapshot friday;
            OpeningTimeSnapshot saturday;
            OpeningTimeSnapshot sunday;
        }

        @Value
        @Builder
        public static class OpeningTimeSnapshot {
            boolean open24h;
            boolean closed;
            @JsonSerialize(using = LocalTimeSerializer.class)
            LocalTime open;
            @JsonSerialize(using = LocalTimeSerializer.class)
            LocalTime close;
        }
    }
}