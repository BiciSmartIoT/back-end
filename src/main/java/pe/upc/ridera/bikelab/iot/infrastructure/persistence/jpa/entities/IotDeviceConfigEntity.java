package pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceConfigResource;

@Entity
@Table(name = "iot_device_configs")
public class IotDeviceConfigEntity {

    @Id
    @Column(name = "device_id", length = 80)
    private String deviceId;

    @Column(name = "speed_limit_kmph", nullable = false)
    private double speedLimitKmph;

    @Column(name = "geofence_center_lat", nullable = false)
    private double geofenceCenterLat;

    @Column(name = "geofence_center_lon", nullable = false)
    private double geofenceCenterLon;

    @Column(name = "geofence_radius_meters", nullable = false)
    private double geofenceRadiusMeters;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected IotDeviceConfigEntity() {
    }

    public IotDeviceConfigEntity(String deviceId, double speedLimitKmph, double geofenceCenterLat,
            double geofenceCenterLon, double geofenceRadiusMeters, Instant updatedAt) {
        this.deviceId = deviceId;
        this.speedLimitKmph = speedLimitKmph;
        this.geofenceCenterLat = geofenceCenterLat;
        this.geofenceCenterLon = geofenceCenterLon;
        this.geofenceRadiusMeters = geofenceRadiusMeters;
        this.updatedAt = updatedAt;
    }

    public IotDeviceConfigResource toResource() {
        return new IotDeviceConfigResource(deviceId, speedLimitKmph, geofenceCenterLat, geofenceCenterLon,
                geofenceRadiusMeters, updatedAt);
    }
}
