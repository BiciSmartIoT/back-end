package pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceCommandResource;

@Entity
@Table(name = "iot_device_commands")
public class IotDeviceCommandEntity {

    @Id
    @Column(name = "command_id")
    private UUID commandId;

    @Column(name = "device_id", nullable = false, length = 80)
    private String deviceId;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(nullable = false, length = 160)
    private String reason;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "ack_message", length = 160)
    private String ackMessage;

    protected IotDeviceCommandEntity() {
    }

    public IotDeviceCommandEntity(UUID commandId, String deviceId, String type, String reason, String status,
            Instant createdAt, Instant deliveredAt, Instant acknowledgedAt, String ackMessage) {
        this.commandId = commandId;
        this.deviceId = deviceId;
        this.type = type;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
        this.deliveredAt = deliveredAt;
        this.acknowledgedAt = acknowledgedAt;
        this.ackMessage = ackMessage;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void markDelivered(Instant deliveredAt) {
        this.status = "DELIVERED";
        this.deliveredAt = deliveredAt;
    }

    public void markAcknowledged(String status, String message, Instant acknowledgedAt) {
        this.status = status;
        this.ackMessage = message;
        this.acknowledgedAt = acknowledgedAt;
    }

    public IotDeviceCommandResource toResource() {
        return new IotDeviceCommandResource(commandId, deviceId, type, reason, status, createdAt, deliveredAt,
                acknowledgedAt, ackMessage);
    }
}
