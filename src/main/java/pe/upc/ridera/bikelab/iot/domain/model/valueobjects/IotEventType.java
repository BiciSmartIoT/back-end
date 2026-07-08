package pe.upc.ridera.bikelab.iot.domain.model.valueobjects;

public enum IotEventType {
    SPEED_ALERT,
    NEAR_LIMIT,
    GPS_UPDATE,
    GEOFENCE_OUTSIDE,
    UNLOCKED,
    LOCKED,
    COMMAND_ACK,
    CONFIG_UPDATED,
    RESET,
    HEARTBEAT
}
