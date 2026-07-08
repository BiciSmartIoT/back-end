ALTER TABLE vehicles
    ADD COLUMN device_id VARCHAR(80);

CREATE UNIQUE INDEX uq_vehicles_device_id ON vehicles(device_id) WHERE device_id IS NOT NULL;

CREATE TABLE iot_devices (
    device_id VARCHAR(80) PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_seen_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE iot_device_configs (
    device_id VARCHAR(80) PRIMARY KEY REFERENCES iot_devices(device_id) ON DELETE CASCADE,
    speed_limit_kmph DOUBLE PRECISION NOT NULL,
    geofence_center_lat DOUBLE PRECISION NOT NULL,
    geofence_center_lon DOUBLE PRECISION NOT NULL,
    geofence_radius_meters DOUBLE PRECISION NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE iot_device_states (
    device_id VARCHAR(80) PRIMARY KEY REFERENCES iot_devices(device_id) ON DELETE CASCADE,
    event_type VARCHAR(40) NOT NULL,
    blocked BOOLEAN NOT NULL,
    message VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    speed_kmph DOUBLE PRECISION,
    inside_geofence BOOLEAN,
    lock_state VARCHAR(40),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE iot_device_commands (
    command_id UUID PRIMARY KEY,
    device_id VARCHAR(80) NOT NULL REFERENCES iot_devices(device_id) ON DELETE CASCADE,
    type VARCHAR(40) NOT NULL,
    reason VARCHAR(160) NOT NULL,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    delivered_at TIMESTAMP WITH TIME ZONE,
    acknowledged_at TIMESTAMP WITH TIME ZONE,
    ack_message VARCHAR(160)
);

CREATE INDEX idx_iot_device_commands_next ON iot_device_commands(device_id, status, created_at);
