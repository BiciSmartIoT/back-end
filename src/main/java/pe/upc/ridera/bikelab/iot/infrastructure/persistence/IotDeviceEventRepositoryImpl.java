package pe.upc.ridera.bikelab.iot.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import pe.upc.ridera.bikelab.iot.domain.model.aggregates.IotDeviceEvent;
import pe.upc.ridera.bikelab.iot.domain.persistence.IotDeviceEventRepository;
import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities.IotDeviceEventEntity;
import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.repositories.IotDeviceEventJpaRepository;

@Repository
public class IotDeviceEventRepositoryImpl implements IotDeviceEventRepository {

    private final IotDeviceEventJpaRepository jpaRepository;

    public IotDeviceEventRepositoryImpl(IotDeviceEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public IotDeviceEvent save(IotDeviceEvent event) {
        return jpaRepository.save(IotDeviceEventEntity.fromAggregate(event)).toAggregate();
    }

    @Override
    public Optional<IotDeviceEvent> findLatest() {
        return jpaRepository.findAllByOrderByReceivedAtDesc(PageRequest.of(0, 1)).stream()
                .findFirst()
                .map(IotDeviceEventEntity::toAggregate);
    }

    @Override
    public List<IotDeviceEvent> findRecent(int limit) {
        return jpaRepository.findAllByOrderByReceivedAtDesc(PageRequest.of(0, limit)).stream()
                .map(IotDeviceEventEntity::toAggregate)
                .toList();
    }
}
