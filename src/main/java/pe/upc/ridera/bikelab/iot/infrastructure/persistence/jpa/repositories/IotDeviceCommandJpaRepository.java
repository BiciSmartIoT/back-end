package pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities.IotDeviceCommandEntity;

public interface IotDeviceCommandJpaRepository extends JpaRepository<IotDeviceCommandEntity, UUID> {

    Optional<IotDeviceCommandEntity> findFirstByDeviceIdAndStatusOrderByCreatedAtAsc(String deviceId, String status);
}
