package devices.configuration.device;

import devices.configuration.device.reads.DevicePin;
import devices.configuration.device.reads.DeviceReadsEntity;
import devices.configuration.device.reads.DeviceReadsRepository;
import devices.configuration.device.reads.DeviceSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class DevicesController {

    private final DeviceReadsRepository repository;
    private final DeviceService service;

    @Transactional(readOnly = true)
    @GetMapping(path = "/devices", params = {"page", "size"},
            produces = APPLICATION_JSON_VALUE)
    public Page<DeviceSnapshot> get(@RequestParam String provider, Pageable pageable) {
        return repository.findAllByProvider(provider, pageable)
                .map(DeviceReadsEntity::getDetails);
    }

    @Transactional(readOnly = true)
    @GetMapping(path = "/devices", params = {"view=summary"},
            produces = APPLICATION_JSON_VALUE)
    public Page<DeviceSummary> getSummary(@RequestParam String provider, Pageable pageable) {
        return repository.findAllByProvider(provider, pageable)
                .map(DeviceReadsEntity::getSummary);
    }

    @Transactional(readOnly = true)
    @GetMapping(path = "/devices", params = {"view=pins"},
            produces = APPLICATION_JSON_VALUE)
    public List<DevicePin> getPins(@RequestParam String provider) {
        return repository.findAllByProvider(provider)
                .map(DeviceReadsEntity::getPin)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @GetMapping(path = "/devices/{deviceId}",
            produces = APPLICATION_JSON_VALUE)
    public Optional<DeviceSnapshot> get(@PathVariable String deviceId) {
        return repository.findById(deviceId)
                .map(DeviceReadsEntity::getDetails);
    }

    @PatchMapping(path = "/devices/{deviceId}",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public DeviceSnapshot patchStation(@PathVariable String deviceId,
                                       @RequestBody @Valid UpdateDevice update) {
        return service.update(deviceId, update)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
