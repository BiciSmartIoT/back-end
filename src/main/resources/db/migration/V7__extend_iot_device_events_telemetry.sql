ALTER TABLE iot_device_events
    ADD COLUMN latitude DOUBLE PRECISION,
    ADD COLUMN longitude DOUBLE PRECISION,
    ADD COLUMN speed_kmph DOUBLE PRECISION,
    ADD COLUMN inside_geofence BOOLEAN,
    ADD COLUMN lock_state VARCHAR(40);

