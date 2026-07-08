package pe.upc.ridera.bikelab.vehicles.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import pe.upc.ridera.bikelab.vehicles.domain.model.aggregates.Vehicle;
import pe.upc.ridera.bikelab.vehicles.domain.model.entities.AvailabilitySlot;
import pe.upc.ridera.bikelab.vehicles.domain.model.valueobjects.VehicleStatus;
import pe.upc.ridera.bikelab.vehicles.domain.persistence.VehicleRepository;
import pe.upc.ridera.bikelab.vehicles.infrastructure.persistence.jpa.entities.VehicleAvailabilityEntity;
import pe.upc.ridera.bikelab.vehicles.infrastructure.persistence.jpa.entities.VehicleEntity;
import pe.upc.ridera.bikelab.vehicles.infrastructure.persistence.jpa.repositories.VehicleJpaRepository;

/**
 * Esta clase implementa el repositorio de vehículos utilizando Spring Data JPA como tecnología de persistencia.
 */ 
@Repository
public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehicleJpaRepository vehicleJpaRepository;

    public VehicleRepositoryImpl(VehicleJpaRepository vehicleJpaRepository) {
        this.vehicleJpaRepository = vehicleJpaRepository;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleEntity entity = toEntity(vehicle);
        VehicleEntity saved = vehicleJpaRepository.save(entity);
        return toAggregate(saved);
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return vehicleJpaRepository.findById(id).map(this::toAggregate);
    }

    @Override
    public List<Vehicle> findByOwnerId(UUID ownerId) {
        return vehicleJpaRepository.findByOwnerId(ownerId).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<Vehicle> searchByBoundingBox(double minLat, double maxLat, double minLng, double maxLng) {
        return vehicleJpaRepository.searchByBoundingBox(minLat, maxLat, minLng, maxLng).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleJpaRepository.findAll().stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return vehicleJpaRepository.count();
    }

    @Override
    public long countByStatus(VehicleStatus status) {
        return vehicleJpaRepository.countByStatus(status.name());
    }

    private VehicleEntity toEntity(Vehicle vehicle) {
        VehicleEntity entity = new VehicleEntity(vehicle.getId(), vehicle.getOwnerId(), vehicle.getStatus().name(),
                vehicle.getTitle(), vehicle.getDescription(), vehicle.getHourlyPrice(), vehicle.getLatitude(),
                vehicle.getLongitude(), vehicle.getDeviceId(), vehicle.getRatingAvg(), vehicle.getCreatedAt(),
                vehicle.getUpdatedAt());
        List<VehicleAvailabilityEntity> availabilityEntities = vehicle.getAvailabilitySlots().stream()
                .map(slot -> new VehicleAvailabilityEntity(slot.getId(), entity, slot.getStartAt(), slot.getEndAt()))
                .collect(Collectors.toList());
        entity.setAvailability(availabilityEntities);
        return entity;
    }

    private Vehicle toAggregate(VehicleEntity entity) {
        List<AvailabilitySlot> slots = entity.getAvailability().stream()
                .map(slot -> new AvailabilitySlot(slot.getId(), slot.getStartAt(), slot.getEndAt()))
                .collect(Collectors.toList());
        return Vehicle.restore(entity.getId(), entity.getOwnerId(), VehicleStatus.valueOf(entity.getStatus()),
                entity.getTitle(), entity.getDescription(), entity.getHourlyPrice(), entity.getLatitude(),
                entity.getLongitude(), entity.getDeviceId(), entity.getRatingAvg(), entity.getCreatedAt(),
                entity.getUpdatedAt(), slots);
    }
}
