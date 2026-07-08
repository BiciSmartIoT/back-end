package pe.upc.ridera.bikelab.iot.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateIotDeviceConfigRequest(
        @Positive double speedLimitKmph,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double geofenceCenterLat,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double geofenceCenterLon,
        @Positive double geofenceRadiusMeters) {
}
