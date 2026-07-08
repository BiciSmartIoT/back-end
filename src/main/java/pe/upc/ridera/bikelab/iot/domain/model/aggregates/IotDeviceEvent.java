package pe.upc.ridera.bikelab.iot.domain.model.aggregates;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import pe.upc.ridera.bikelab.iot.domain.model.valueobjects.IotEventType;

public class IotDeviceEvent {

    private final UUID id;
    private final String deviceId;
    private final IotEventType eventType;
    private final boolean blocked;
    private final String message;
    private final Instant occurredAt;
    private final Instant receivedAt;
    private final Double latitude;
    private final Double longitude;
    private final Double speedKmph;
    private final Boolean insideGeofence;
    private final String lockState;

    private IotDeviceEvent(UUID id, String deviceId, IotEventType eventType, boolean blocked, String message,
            Instant occurredAt, Instant receivedAt, Double latitude, Double longitude, Double speedKmph,
            Boolean insideGeofence, String lockState) {
        this.id = Objects.requireNonNull(id, "id");
        this.deviceId = Objects.requireNonNull(deviceId, "deviceId");
        this.eventType = Objects.requireNonNull(eventType, "eventType");
        this.blocked = blocked;
        this.message = Objects.requireNonNull(message, "message");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.receivedAt = Objects.requireNonNull(receivedAt, "receivedAt");
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedKmph = speedKmph;
        this.insideGeofence = insideGeofence;
        this.lockState = lockState;
    }

    public static IotDeviceEvent create(String deviceId, IotEventType eventType, boolean blocked, String message,
            Instant occurredAt, Double latitude, Double longitude, Double speedKmph, Boolean insideGeofence,
            String lockState) {
        Instant now = Instant.now();
        return new IotDeviceEvent(UUID.randomUUID(), deviceId, eventType, blocked, message,
                occurredAt == null ? now : occurredAt, now, latitude, longitude, speedKmph, insideGeofence, lockState);
    }

    public static IotDeviceEvent restore(UUID id, String deviceId, IotEventType eventType, boolean blocked,
            String message, Instant occurredAt, Instant receivedAt, Double latitude, Double longitude, Double speedKmph,
            Boolean insideGeofence, String lockState) {
        return new IotDeviceEvent(id, deviceId, eventType, blocked, message, occurredAt, receivedAt, latitude,
                longitude, speedKmph, insideGeofence, lockState);
    }

    public UUID getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public IotEventType getEventType() {
        return eventType;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public String getMessage() {
        return message;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getSpeedKmph() {
        return speedKmph;
    }

    public Boolean getInsideGeofence() {
        return insideGeofence;
    }

    public String getLockState() {
        return lockState;
    }
}
