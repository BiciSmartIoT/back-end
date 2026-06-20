package pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities.IotDeviceEventEntity;

public interface IotDeviceEventJpaRepository extends JpaRepository<IotDeviceEventEntity, UUID> {

    List<IotDeviceEventEntity> findAllByOrderByReceivedAtDesc(Pageable pageable);
}
