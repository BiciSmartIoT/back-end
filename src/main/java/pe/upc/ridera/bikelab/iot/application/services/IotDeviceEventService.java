package pe.upc.ridera.bikelab.iot.application.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceEventResource;
import pe.upc.ridera.bikelab.iot.domain.model.valueobjects.IotEventType;

public interface IotDeviceEventService {

    IotDeviceEventResource register(String deviceId, IotEventType eventType, boolean blocked, String message,
            Instant occurredAt, Double latitude, Double longitude, Double speedKmph, Boolean insideGeofence,
            String lockState);

    Optional<IotDeviceEventResource> latest();

    List<IotDeviceEventResource> recent(int limit);
}
