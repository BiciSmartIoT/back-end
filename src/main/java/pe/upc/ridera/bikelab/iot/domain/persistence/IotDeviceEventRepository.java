package pe.upc.ridera.bikelab.iot.domain.persistence;

import java.util.List;
import java.util.Optional;

import pe.upc.ridera.bikelab.iot.domain.model.aggregates.IotDeviceEvent;

public interface IotDeviceEventRepository {

    IotDeviceEvent save(IotDeviceEvent event);

    Optional<IotDeviceEvent> findLatest();

    List<IotDeviceEvent> findRecent(int limit);
}
