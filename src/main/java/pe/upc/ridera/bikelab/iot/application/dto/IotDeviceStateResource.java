package pe.upc.ridera.bikelab.iot.application.dto;

import java.time.Instant;

import pe.upc.ridera.bikelab.iot.domain.model.valueobjects.IotEventType;

public record IotDeviceStateResource(
        String deviceId,
        IotEventType eventType,
        boolean blocked,
        String message,
        Double latitude,
        Double longitude,
        Double speedKmph,
        Boolean insideGeofence,
        String lockState,
        Instant updatedAt) {
}
