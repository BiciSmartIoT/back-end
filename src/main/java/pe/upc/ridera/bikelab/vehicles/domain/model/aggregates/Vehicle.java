package pe.upc.ridera.bikelab.vehicles.domain.model.aggregates;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import pe.upc.ridera.bikelab.vehicles.domain.exceptions.AvailabilityConflictException;
import pe.upc.ridera.bikelab.vehicles.domain.exceptions.UnauthorizedVehicleAccessException;
import pe.upc.ridera.bikelab.vehicles.domain.exceptions.VehicleDomainException;
import pe.upc.ridera.bikelab.vehicles.domain.model.entities.AvailabilitySlot;
import pe.upc.ridera.bikelab.vehicles.domain.model.valueobjects.VehicleStatus;

/**
 * Esta clase representa el agregado Vehicle encargado de administrar la información y disponibilidad de un vehículo.
 */ 
public class Vehicle {

    private final UUID id;
    private final UUID ownerId;
    private VehicleStatus status;
    private String title;
    private String description;
    private BigDecimal hourlyPrice;
    private double latitude;
    private double longitude;
    private String deviceId;
    private BigDecimal ratingAvg;
    private Instant createdAt;
    private Instant updatedAt;
    private final List<AvailabilitySlot> availabilitySlots;

    private Vehicle(UUID id, UUID ownerId, VehicleStatus status, String title, String description,
            BigDecimal hourlyPrice, double latitude, double longitude, String deviceId, BigDecimal ratingAvg,
            Instant createdAt, Instant updatedAt, List<AvailabilitySlot> availabilitySlots) {
        this.id = Objects.requireNonNull(id, "id");
        this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
        this.status = Objects.requireNonNull(status, "status");
        this.title = Objects.requireNonNull(title, "title");
        this.description = Objects.requireNonNull(description, "description");
        this.hourlyPrice = Objects.requireNonNull(hourlyPrice, "hourlyPrice");
        this.latitude = latitude;
        this.longitude = longitude;
        this.deviceId = deviceId;
        this.ratingAvg = ratingAvg == null ? BigDecimal.ZERO : ratingAvg;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
        this.updatedAt = updatedAt == null ? this.createdAt : updatedAt;
        this.availabilitySlots = new ArrayList<>(availabilitySlots == null ? List.of() : availabilitySlots);
    }

    public static Vehicle create(UUID id, UUID ownerId, String title, String description, BigDecimal hourlyPrice,
            double latitude, double longitude, String deviceId) {
        if (hourlyPrice == null || hourlyPrice.signum() <= 0) {
            throw new VehicleDomainException("El precio por hora debe ser mayor a cero");
        }
        return new Vehicle(id == null ? UUID.randomUUID() : id, ownerId, VehicleStatus.AVAILABLE, title,
                description, hourlyPrice, latitude, longitude, deviceId, BigDecimal.ZERO, Instant.now(), Instant.now(),
                new ArrayList<>());
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getHourlyPrice() {
        return hourlyPrice;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public BigDecimal getRatingAvg() {
        return ratingAvg;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<AvailabilitySlot> getAvailabilitySlots() {
        return Collections.unmodifiableList(availabilitySlots);
    }

    public void updateDetails(UUID requesterId, String title, String description, BigDecimal hourlyPrice,
            double latitude, double longitude, String deviceId, VehicleStatus desiredStatus, boolean overrideOwnership) {
        if (!overrideOwnership) {
            ensureOwnerOrThrow(requesterId);
        }
        if (hourlyPrice != null && hourlyPrice.signum() <= 0) {
            throw new VehicleDomainException("El precio por hora debe ser mayor a cero");
        }
        this.title = title != null ? title : this.title;
        this.description = description != null ? description : this.description;
        this.hourlyPrice = hourlyPrice != null ? hourlyPrice : this.hourlyPrice;
        this.latitude = latitude;
        this.longitude = longitude;
        if (deviceId != null) {
            this.deviceId = deviceId.isBlank() ? null : deviceId;
        }
        if (desiredStatus != null) {
            this.status = desiredStatus;
        }
        touch();
    }

    public AvailabilitySlot blockAvailability(UUID requesterId, Instant startAt, Instant endAt) {
        ensureOwnerOrThrow(requesterId);
        Objects.requireNonNull(startAt, "startAt");
        Objects.requireNonNull(endAt, "endAt");
        AvailabilitySlot newSlot = new AvailabilitySlot(UUID.randomUUID(), startAt, endAt);
        for (AvailabilitySlot slot : availabilitySlots) {
            if (slot.overlaps(startAt, endAt)) {
                throw new AvailabilityConflictException();
            }
        }
        availabilitySlots.add(newSlot);
        touch();
        return newSlot;
    }

    public void unblockAvailability(UUID requesterId, UUID slotId) {
        ensureOwnerOrThrow(requesterId);
        availabilitySlots.removeIf(slot -> slot.getId().equals(slotId));
        touch();
    }

    public void markReserved(Instant startAt, Instant endAt) {
        if (status == VehicleStatus.UNAVAILABLE) {
            throw new VehicleDomainException("No se puede reservar un vehículo no disponible");
        }
        ensureNoConflicts(startAt, endAt);
        this.status = VehicleStatus.RESERVED;
        touch();
    }

    public void markAvailable() {
        this.status = VehicleStatus.AVAILABLE;
        touch();
    }

    public void markInService() {
        if (status != VehicleStatus.RESERVED) {
            throw new VehicleDomainException("Solo se puede iniciar el servicio desde reservado");
        }
        this.status = VehicleStatus.IN_SERVICE;
        touch();
    }

    public void markFinished() {
        this.status = VehicleStatus.AVAILABLE;
        touch();
    }

    public void updateRating(BigDecimal ratingAvg) {
        if (ratingAvg != null && ratingAvg.signum() >= 0) {
            this.ratingAvg = ratingAvg;
            touch();
        }
    }

    private void ensureOwnerOrThrow(UUID requesterId) {
        if (requesterId == null || !ownerId.equals(requesterId)) {
            throw new UnauthorizedVehicleAccessException(requesterId);
        }
    }

    private void ensureNoConflicts(Instant startAt, Instant endAt) {
        for (AvailabilitySlot slot : availabilitySlots) {
            if (slot.overlaps(startAt, endAt)) {
                throw new AvailabilityConflictException();
            }
        }
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public static Vehicle restore(UUID id, UUID ownerId, VehicleStatus status, String title, String description,
            BigDecimal hourlyPrice, double latitude, double longitude, String deviceId, BigDecimal ratingAvg,
            Instant createdAt, Instant updatedAt, List<AvailabilitySlot> availabilitySlots) {
        return new Vehicle(id, ownerId, status, title, description, hourlyPrice, latitude, longitude, deviceId, ratingAvg,
                createdAt, updatedAt, availabilitySlots);
    }
}
