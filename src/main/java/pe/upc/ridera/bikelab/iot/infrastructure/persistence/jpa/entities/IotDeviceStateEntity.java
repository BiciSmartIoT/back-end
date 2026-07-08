package pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceStateResource;
import pe.upc.ridera.bikelab.iot.domain.model.valueobjects.IotEventType;

@Entity
@Table(name = "iot_device_states")
public class IotDeviceStateEntity {

    @Id
    @Column(name = "device_id", length = 80)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private IotEventType eventType;

    @Column(nullable = false)
    private boolean blocked;

    @Column(nullable = false, length = 255)
    private String message;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "speed_kmph")
    private Double speedKmph;

    @Column(name = "inside_geofence")
    private Boolean insideGeofence;

    @Column(name = "lock_state", length = 40)
    private String lockState;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected IotDeviceStateEntity() {
    }

    public IotDeviceStateEntity(String deviceId, IotEventType eventType, boolean blocked, String message,
            Double latitude, Double longitude, Double speedKmph, Boolean insideGeofence, String lockState,
            Instant updatedAt) {
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.blocked = blocked;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedKmph = speedKmph;
        this.insideGeofence = insideGeofence;
        this.lockState = lockState;
        this.updatedAt = updatedAt;
    }

    public IotDeviceStateResource toResource() {
        return new IotDeviceStateResource(deviceId, eventType, blocked, message, latitude, longitude, speedKmph,
                insideGeofence, lockState, updatedAt);
    }
}
