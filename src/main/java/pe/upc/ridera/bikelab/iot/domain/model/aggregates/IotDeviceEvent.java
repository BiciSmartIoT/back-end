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

    private IotDeviceEvent(UUID id, String deviceId, IotEventType eventType, boolean blocked, String message,
            Instant occurredAt, Instant receivedAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.deviceId = Objects.requireNonNull(deviceId, "deviceId");
        this.eventType = Objects.requireNonNull(eventType, "eventType");
        this.blocked = blocked;
        this.message = Objects.requireNonNull(message, "message");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.receivedAt = Objects.requireNonNull(receivedAt, "receivedAt");
    }

    public static IotDeviceEvent create(String deviceId, IotEventType eventType, boolean blocked, String message,
            Instant occurredAt) {
        Instant now = Instant.now();
        return new IotDeviceEvent(UUID.randomUUID(), deviceId, eventType, blocked, message,
                occurredAt == null ? now : occurredAt, now);
    }

    public static IotDeviceEvent restore(UUID id, String deviceId, IotEventType eventType, boolean blocked,
            String message, Instant occurredAt, Instant receivedAt) {
        return new IotDeviceEvent(id, deviceId, eventType, blocked, message, occurredAt, receivedAt);
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
}
