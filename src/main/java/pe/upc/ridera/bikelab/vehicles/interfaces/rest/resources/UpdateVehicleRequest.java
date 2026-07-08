package pe.upc.ridera.bikelab.vehicles.interfaces.rest.resources;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import pe.upc.ridera.bikelab.vehicles.domain.model.valueobjects.VehicleStatus;

/**
 * Esta clase captura los campos editables cuando un propietario desea modificar su vehículo.
 */ 
public record UpdateVehicleRequest(
        String title,
        String description,
        @DecimalMin("0.1") BigDecimal hourlyPrice,
        Double latitude,
        Double longitude,
        @Size(max = 80) String deviceId,
        VehicleStatus desiredStatus) {
}
