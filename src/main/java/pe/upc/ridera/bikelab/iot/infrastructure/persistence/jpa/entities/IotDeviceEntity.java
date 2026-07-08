package pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iot_devices")
public class IotDeviceEntity {

    @Id
    @Column(name = "device_id", length = 80)
    private String deviceId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    protected IotDeviceEntity() {
    }

    public IotDeviceEntity(String deviceId, Instant createdAt, Instant lastSeenAt) {
        this.deviceId = deviceId;
        this.createdAt = createdAt;
        this.lastSeenAt = lastSeenAt;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void markSeen(Instant seenAt) {
        this.lastSeenAt = seenAt;
    }
}
