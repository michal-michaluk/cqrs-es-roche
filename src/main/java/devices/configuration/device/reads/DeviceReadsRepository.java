package devices.configuration.device.reads;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.stream.Stream;

public interface DeviceReadsRepository extends PagingAndSortingRepository<DeviceReadsEntity, String> {

    Stream<DeviceReadsEntity> findAllByProvider(String provider);

    Page<DeviceReadsEntity> findAllByProvider(String provider, Pageable pageable);

}
