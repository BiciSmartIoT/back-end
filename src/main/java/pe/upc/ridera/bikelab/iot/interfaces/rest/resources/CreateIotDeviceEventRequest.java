package pe.upc.ridera.bikelab.iot.interfaces.rest.resources;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.upc.ridera.bikelab.iot.domain.model.valueobjects.IotEventType;

public record CreateIotDeviceEventRequest(
        @NotBlank @Size(max = 80) String deviceId,
        @NotNull IotEventType eventType,
        boolean blocked,
        @NotBlank @Size(max = 255) String message,
        Instant occurredAt,
        Double latitude,
        Double longitude,
        Double speedKmph,
        Boolean insideGeofence,
        String lockState) {
}
