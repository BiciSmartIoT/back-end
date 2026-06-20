package pe.upc.ridera.bikelab.iot.application.internal;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceEventResource;
import pe.upc.ridera.bikelab.iot.application.services.IotDeviceEventService;
import pe.upc.ridera.bikelab.iot.domain.model.aggregates.IotDeviceEvent;
import pe.upc.ridera.bikelab.iot.domain.model.valueobjects.IotEventType;
import pe.upc.ridera.bikelab.iot.domain.persistence.IotDeviceEventRepository;

@Service
public class IotDeviceEventServiceImpl implements IotDeviceEventService {

    private static final int MAX_LIMIT = 100;

    private final IotDeviceEventRepository repository;

    public IotDeviceEventServiceImpl(IotDeviceEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public IotDeviceEventResource register(String deviceId, IotEventType eventType, boolean blocked, String message,
            Instant occurredAt) {
        IotDeviceEvent event = IotDeviceEvent.create(deviceId, eventType, blocked, message, occurredAt);
        return toResource(repository.save(event));
    }

    @Override
    public Optional<IotDeviceEventResource> latest() {
        return repository.findLatest().map(this::toResource);
    }

    @Override
    public List<IotDeviceEventResource> recent(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, MAX_LIMIT));
        return repository.findRecent(safeLimit).stream()
                .map(this::toResource)
                .toList();
    }

    private IotDeviceEventResource toResource(IotDeviceEvent event) {
        return new IotDeviceEventResource(event.getId(), event.getDeviceId(), event.getEventType(), event.isBlocked(),
                event.getMessage(), event.getOccurredAt(), event.getReceivedAt());
    }
}
