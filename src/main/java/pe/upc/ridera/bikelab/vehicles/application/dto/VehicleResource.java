package pe.upc.ridera.bikelab.vehicles.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import pe.upc.ridera.bikelab.vehicles.domain.model.valueobjects.VehicleStatus;

/**
 * Esta clase prepara los datos del vehículo para consumo externo vía API o aplicación.
 */ 
public record VehicleResource(
        UUID id,
        UUID ownerId,
        VehicleStatus status,
        String title,
        String description,
        BigDecimal hourlyPrice,
        double latitude,
        double longitude,
        String deviceId,
        BigDecimal ratingAvg,
        Instant createdAt,
        Instant updatedAt,
        List<AvailabilitySlotResource> availability) {
}
