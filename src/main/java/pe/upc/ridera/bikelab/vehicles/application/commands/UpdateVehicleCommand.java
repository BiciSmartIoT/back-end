package pe.upc.ridera.bikelab.vehicles.application.commands;

import java.math.BigDecimal;
import java.util.UUID;

import pe.upc.ridera.bikelab.vehicles.domain.model.valueobjects.VehicleStatus;

/**
 * Esta clase transporta los atributos editables de un vehículo para que el propietario los actualice.
 */ 
public record UpdateVehicleCommand(
        UUID requesterId,
        UUID vehicleId,
        String title,
        String description,
        BigDecimal hourlyPrice,
        Double latitude,
        Double longitude,
        String deviceId,
        VehicleStatus desiredStatus,
        boolean overrideOwnership) {
}
