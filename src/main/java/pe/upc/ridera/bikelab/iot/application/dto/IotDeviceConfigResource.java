package pe.upc.ridera.bikelab.iot.application.dto;

import java.time.Instant;

public record IotDeviceConfigResource(
        String deviceId,
        double speedLimitKmph,
        double geofenceCenterLat,
        double geofenceCenterLon,
        double geofenceRadiusMeters,
        Instant updatedAt) {
}
