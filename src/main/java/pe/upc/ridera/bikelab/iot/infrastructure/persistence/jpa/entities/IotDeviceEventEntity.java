package pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import pe.upc.ridera.bikelab.iot.domain.model.aggregates.IotDeviceEvent;
import pe.upc.ridera.bikelab.iot.domain.model.valueobjects.IotEventType;

@Entity
@Table(name = "iot_device_events")
public class IotDeviceEventEntity {

    @Id
    private UUID id;

    @Column(name = "device_id", nullable = false, length = 80)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private IotEventType eventType;

    @Column(nullable = false)
    private boolean blocked;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    protected IotDeviceEventEntity() {
    }

    public IotDeviceEventEntity(UUID id, String deviceId, IotEventType eventType, boolean blocked, String message,
            Instant occurredAt, Instant receivedAt) {
        this.id = id;
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.blocked = blocked;
        this.message = message;
        this.occurredAt = occurredAt;
        this.receivedAt = receivedAt;
    }

    public static IotDeviceEventEntity fromAggregate(IotDeviceEvent event) {
        return new IotDeviceEventEntity(event.getId(), event.getDeviceId(), event.getEventType(), event.isBlocked(),
                event.getMessage(), event.getOccurredAt(), event.getReceivedAt());
    }

    public IotDeviceEvent toAggregate() {
        return IotDeviceEvent.restore(id, deviceId, eventType, blocked, message, occurredAt, receivedAt);
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
