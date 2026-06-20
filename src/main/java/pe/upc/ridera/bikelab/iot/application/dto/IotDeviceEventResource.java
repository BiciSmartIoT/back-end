package pe.upc.ridera.bikelab.iot.application.dto;

import java.time.Instant;
import java.util.UUID;

import pe.upc.ridera.bikelab.iot.domain.model.valueobjects.IotEventType;

public record IotDeviceEventResource(
        UUID id,
        String deviceId,
        IotEventType eventType,
        boolean blocked,
        String message,
        Instant occurredAt,
        Instant receivedAt) {
}
