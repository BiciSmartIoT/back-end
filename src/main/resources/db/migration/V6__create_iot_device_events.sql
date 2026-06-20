CREATE TABLE iot_device_events (
    id UUID PRIMARY KEY,
    device_id VARCHAR(80) NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    blocked BOOLEAN NOT NULL,
    message VARCHAR(255) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    received_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_iot_device_events_device_received
    ON iot_device_events(device_id, received_at DESC);

CREATE INDEX idx_iot_device_events_received
    ON iot_device_events(received_at DESC);
