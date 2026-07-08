package pe.upc.ridera.bikelab.iot.application.dto;

import java.time.Instant;
import java.util.UUID;

public record IotDeviceCommandResource(
        UUID commandId,
        String deviceId,
        String type,
        String reason,
        String status,
        Instant createdAt,
        Instant deliveredAt,
        Instant acknowledgedAt,
        String ackMessage) {
}
