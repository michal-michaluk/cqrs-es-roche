package devices.configuration.device;

import devices.configuration.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static devices.configuration.TestTransaction.transactional;

@IntegrationTest
@Transactional
class DeviceRepositoryTest {

    @Autowired
    private DeviceRepository repository;

    String deviceId = "device " + UUID.randomUUID();

    @Test
    public void shouldSaveAndLoadDevice() {
        //given
        Device device = DeviceFixture.notConfigured(deviceId);
        device.assignTo(new Ownership("operator", "provider"));
        device.updateSettings(Settings.builder()
                .autoStart(true)
                .remoteControl(true)
                .billing(true)
                .reimbursement(true)
                .showOnMap(true)
                .publicAccess(true)
                .build());
        device.updateOpeningHours(OpeningHours.openAt(
                OpeningHours.OpeningTime.open24h(),
                OpeningHours.OpeningTime.open24h(),
                OpeningHours.OpeningTime.open24h(),
                OpeningHours.OpeningTime.open24h(),
                OpeningHours.OpeningTime.open24h(),
                OpeningHours.OpeningTime.opened(8, 17),
                OpeningHours.OpeningTime.closed()
        ));

        // when
        transactional(() -> repository.save(device));
        var result = transactional(() -> repository.get(deviceId));

        // then
        Assertions.assertThat(result.map(Device::toSnapshot))
                .hasValue(device.toSnapshot());
    }

}
