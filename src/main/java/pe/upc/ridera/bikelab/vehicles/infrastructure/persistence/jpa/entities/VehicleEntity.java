package pe.upc.ridera.bikelab.vehicles.infrastructure.persistence.jpa.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Esta entidad JPA representa la tabla principal de vehículos persistidos en la base de datos relacional.
 */ 
@Entity
@Table(name = "vehicles")
public class VehicleEntity {

    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "hourly_price", nullable = false)
    private BigDecimal hourlyPrice;

    @Column(name = "lat", nullable = false)
    private double latitude;

    @Column(name = "lng", nullable = false)
    private double longitude;

    @Column(name = "device_id", length = 80)
    private String deviceId;

    @Column(name = "rating_avg", nullable = false)
    private BigDecimal ratingAvg;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<VehicleAvailabilityEntity> availability = new ArrayList<>();

    public VehicleEntity() {
    }

    public VehicleEntity(UUID id, UUID ownerId, String status, String title, String description, BigDecimal hourlyPrice,
            double latitude, double longitude, String deviceId, BigDecimal ratingAvg, Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.status = status;
        this.title = title;
        this.description = description;
        this.hourlyPrice = hourlyPrice;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deviceId = deviceId;
        this.ratingAvg = ratingAvg;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getStatus() {
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

    public List<VehicleAvailabilityEntity> getAvailability() {
        return availability;
    }

    public void setAvailability(List<VehicleAvailabilityEntity> availability) {
        this.availability = availability;
    }
}
