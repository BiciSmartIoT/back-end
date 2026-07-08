package pe.upc.ridera.bikelab.iot.application.internal;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceCommandResource;
import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceConfigResource;
import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceStateResource;
import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities.IotDeviceCommandEntity;
import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities.IotDeviceConfigEntity;
import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities.IotDeviceEntity;
import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities.IotDeviceStateEntity;
import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.repositories.IotDeviceCommandJpaRepository;
import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.repositories.IotDeviceConfigJpaRepository;
import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.repositories.IotDeviceJpaRepository;
import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.repositories.IotDeviceStateJpaRepository;
import pe.upc.ridera.bikelab.iot.interfaces.rest.resources.AcknowledgeIotDeviceCommandRequest;
import pe.upc.ridera.bikelab.iot.interfaces.rest.resources.CreateIotDeviceCommandRequest;
import pe.upc.ridera.bikelab.iot.interfaces.rest.resources.CreateIotDeviceEventRequest;
import pe.upc.ridera.bikelab.iot.interfaces.rest.resources.UpdateIotDeviceConfigRequest;

@Service
public class IotDeviceEdgeStateService {

    private static final double DEFAULT_SPEED_LIMIT_KMPH = 30.0;
    private static final double DEFAULT_GEOFENCE_LAT = -12.0464;
    private static final double DEFAULT_GEOFENCE_LON = -77.0428;
    private static final double DEFAULT_GEOFENCE_RADIUS_METERS = 100.0;

    private final IotDeviceJpaRepository deviceRepository;
    private final IotDeviceConfigJpaRepository configRepository;
    private final IotDeviceStateJpaRepository stateRepository;
    private final IotDeviceCommandJpaRepository commandRepository;

    public IotDeviceEdgeStateService(IotDeviceJpaRepository deviceRepository,
            IotDeviceConfigJpaRepository configRepository,
            IotDeviceStateJpaRepository stateRepository,
            IotDeviceCommandJpaRepository commandRepository) {
        this.deviceRepository = deviceRepository;
        this.configRepository = configRepository;
        this.stateRepository = stateRepository;
        this.commandRepository = commandRepository;
    }

    @Transactional
    public IotDeviceConfigResource getConfig(String deviceId) {
        ensureDevice(deviceId, false);
        return configRepository.findById(deviceId)
                .orElseGet(() -> configRepository.save(defaultConfig(deviceId)))
                .toResource();
    }

    @Transactional
    public IotDeviceConfigResource updateConfig(String deviceId, UpdateIotDeviceConfigRequest request) {
        ensureDevice(deviceId, false);
        IotDeviceConfigEntity config = new IotDeviceConfigEntity(deviceId, request.speedLimitKmph(),
                request.geofenceCenterLat(), request.geofenceCenterLon(), request.geofenceRadiusMeters(),
                Instant.now());
        IotDeviceConfigEntity saved = configRepository.save(config);
        enqueueCommand(deviceId, new CreateIotDeviceCommandRequest("SET_CONFIG", "Configuracion IoT actualizada"));
        return saved.toResource();
    }

    @Transactional
    public IotDeviceCommandResource enqueueCommand(String deviceId, CreateIotDeviceCommandRequest request) {
        ensureDevice(deviceId, false);
        IotDeviceCommandEntity command = new IotDeviceCommandEntity(UUID.randomUUID(), deviceId, request.type(),
                request.reason(), "PENDING", Instant.now(), null, null, null);
        return commandRepository.save(command).toResource();
    }

    @Transactional
    public Optional<IotDeviceCommandResource> nextCommand(String deviceId) {
        ensureDevice(deviceId, true);
        return commandRepository.findFirstByDeviceIdAndStatusOrderByCreatedAtAsc(deviceId, "PENDING")
                .map(command -> {
                    command.markDelivered(Instant.now());
                    return commandRepository.save(command).toResource();
                });
    }

    @Transactional
    public Optional<IotDeviceCommandResource> acknowledgeCommand(String deviceId, UUID commandId,
            AcknowledgeIotDeviceCommandRequest request) {
        ensureDevice(deviceId, true);
        return commandRepository.findById(commandId)
                .filter(command -> command.getDeviceId().equals(deviceId))
                .map(command -> {
                    command.markAcknowledged(request.status(), request.message(), Instant.now());
                    return commandRepository.save(command).toResource();
                });
    }

    @Transactional(readOnly = true)
    public Optional<IotDeviceStateResource> state(String deviceId) {
        return stateRepository.findById(deviceId).map(IotDeviceStateEntity::toResource);
    }

    @Transactional
    public IotDeviceStateResource recordEvent(CreateIotDeviceEventRequest request) {
        ensureDevice(request.deviceId(), true);
        IotDeviceStateEntity state = new IotDeviceStateEntity(request.deviceId(), request.eventType(), request.blocked(),
                request.message(), request.latitude(), request.longitude(), request.speedKmph(),
                request.insideGeofence(), request.lockState(), Instant.now());
        return stateRepository.save(state).toResource();
    }

    private IotDeviceConfigEntity defaultConfig(String deviceId) {
        return new IotDeviceConfigEntity(deviceId, DEFAULT_SPEED_LIMIT_KMPH, DEFAULT_GEOFENCE_LAT, DEFAULT_GEOFENCE_LON,
                DEFAULT_GEOFENCE_RADIUS_METERS, Instant.now());
    }

    private void ensureDevice(String deviceId, boolean markSeen) {
        Instant now = Instant.now();
        IotDeviceEntity device = deviceRepository.findById(deviceId)
                .orElseGet(() -> new IotDeviceEntity(deviceId, now, null));
        if (markSeen) {
            device.markSeen(now);
        }
        deviceRepository.save(device);
    }
}
