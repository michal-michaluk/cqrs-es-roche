package devices.configuration.device.reads;

import devices.configuration.device.DeviceSnapshot;
import devices.configuration.device.Ownership;
import devices.configuration.device.events.DeviceStatuses;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Optional;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "device_reads")
@NoArgsConstructor
public class DeviceReadsEntity {

    @Id
    private String deviceId;
    @Version
    private Long version;
    private String operator;
    private String provider;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private DevicePin pin;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private DeviceSummary summary;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private DeviceSnapshot details;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private DeviceStatuses statuses;

    public DeviceReadsEntity(String deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceReadsEntity setOwnership(Ownership ownership) {
        operator = Optional.ofNullable(ownership).map(Ownership::getOperator).orElse(null);
        provider = Optional.ofNullable(ownership).map(Ownership::getProvider).orElse(null);
        return this;
    }
}
