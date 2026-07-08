package pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.upc.ridera.bikelab.iot.infrastructure.persistence.jpa.entities.IotDeviceConfigEntity;

public interface IotDeviceConfigJpaRepository extends JpaRepository<IotDeviceConfigEntity, String> {
}
