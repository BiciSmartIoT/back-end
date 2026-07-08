package pe.upc.ridera.bikelab.vehicles.application.commands;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Esta clase encapsula los datos necesarios para registrar un nuevo vehículo en el catálogo.
 */ 
public record CreateVehicleCommand(
        UUID ownerId,
        String title,
        String description,
        BigDecimal hourlyPrice,
        double latitude,
        double longitude,
        String deviceId) {
}
