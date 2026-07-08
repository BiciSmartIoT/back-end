package pe.upc.ridera.bikelab.vehicles.application.internal.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import pe.upc.ridera.bikelab.vehicles.application.dto.AvailabilitySlotResource;
import pe.upc.ridera.bikelab.vehicles.application.dto.VehicleResource;
import pe.upc.ridera.bikelab.vehicles.domain.model.aggregates.Vehicle;
import pe.upc.ridera.bikelab.vehicles.domain.model.entities.AvailabilitySlot;

/**
 * Esta clase centraliza las transformaciones entre el agregado Vehicle y sus recursos expuestos por la API.
 */ 
@Component
public class VehicleMapper {

    public VehicleResource toResource(Vehicle vehicle) {
        return new VehicleResource(
                vehicle.getId(),
                vehicle.getOwnerId(),
                vehicle.getStatus(),
                vehicle.getTitle(),
                vehicle.getDescription(),
                vehicle.getHourlyPrice(),
                vehicle.getLatitude(),
                vehicle.getLongitude(),
                vehicle.getDeviceId(),
                vehicle.getRatingAvg(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt(),
                mapAvailability(vehicle.getAvailabilitySlots()));
    }

    private List<AvailabilitySlotResource> mapAvailability(List<AvailabilitySlot> slots) {
        return slots.stream()
                .map(slot -> new AvailabilitySlotResource(slot.getId(), slot.getStartAt(), slot.getEndAt()))
                .collect(Collectors.toList());
    }
}
