package pe.upc.ridera.bikelab.vehicles.application.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upc.ridera.bikelab.renting.domain.model.events.BookingCancelledEvent;
import pe.upc.ridera.bikelab.renting.domain.model.events.BookingCreatedEvent;
import pe.upc.ridera.bikelab.renting.domain.model.events.RentalFinishedEvent;
import pe.upc.ridera.bikelab.renting.domain.model.events.RentalStartedEvent;
import pe.upc.ridera.bikelab.vehicles.application.commands.BlockAvailabilityCommand;
import pe.upc.ridera.bikelab.vehicles.application.commands.CreateVehicleCommand;
import pe.upc.ridera.bikelab.vehicles.application.commands.UnblockAvailabilityCommand;
import pe.upc.ridera.bikelab.vehicles.application.commands.UpdateVehicleCommand;
import pe.upc.ridera.bikelab.vehicles.application.dto.AvailabilitySlotResource;
import pe.upc.ridera.bikelab.vehicles.application.dto.VehicleResource;
import pe.upc.ridera.bikelab.vehicles.application.internal.mappers.VehicleMapper;
import pe.upc.ridera.bikelab.vehicles.application.services.VehicleCommandService;
import pe.upc.ridera.bikelab.vehicles.domain.exceptions.VehicleNotFoundException;
import pe.upc.ridera.bikelab.vehicles.domain.model.aggregates.Vehicle;
import pe.upc.ridera.bikelab.vehicles.domain.persistence.VehicleRepository;

/**
 * Esta clase implementa los casos de uso de escritura del contexto de vehículos y coordina las transiciones de estado.
 */ 
@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleCommandServiceImpl.class);

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper mapper;

    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository, VehicleMapper mapper) {
        this.vehicleRepository = vehicleRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public VehicleResource createVehicle(CreateVehicleCommand command) {
        Vehicle vehicle = Vehicle.create(null, command.ownerId(), command.title(),
                command.description(), command.hourlyPrice(), command.latitude(), command.longitude(),
                command.deviceId());
        Vehicle stored = vehicleRepository.save(vehicle);
        return mapper.toResource(stored);
    }

    @Override
    @Transactional
    public VehicleResource updateVehicle(UpdateVehicleCommand command) {
        Vehicle vehicle = vehicleRepository.findById(command.vehicleId()).orElseThrow(() -> new VehicleNotFoundException(command.vehicleId()));
        double latitude = command.latitude() != null ? command.latitude() : vehicle.getLatitude();
        double longitude = command.longitude() != null ? command.longitude() : vehicle.getLongitude();
        vehicle.updateDetails(command.requesterId(), command.title(), command.description(), command.hourlyPrice(),
                latitude, longitude, command.deviceId(), command.desiredStatus(), command.overrideOwnership());
        Vehicle saved = vehicleRepository.save(vehicle);
        return mapper.toResource(saved);
    }

    @Override
    @Transactional
    public AvailabilitySlotResource blockAvailability(BlockAvailabilityCommand command) {
        Vehicle vehicle = vehicleRepository.findById(command.vehicleId()).orElseThrow(() -> new VehicleNotFoundException(command.vehicleId()));
        var slot = vehicle.blockAvailability(command.requesterId(), command.startAt(), command.endAt());
        vehicleRepository.save(vehicle);
        return new AvailabilitySlotResource(slot.getId(), slot.getStartAt(), slot.getEndAt());
    }

    @Override
    @Transactional
    public void unblockAvailability(UnblockAvailabilityCommand command) {
        Vehicle vehicle = vehicleRepository.findById(command.vehicleId()).orElseThrow(() -> new VehicleNotFoundException(command.vehicleId()));
        vehicle.unblockAvailability(command.requesterId(), command.slotId());
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void handleBookingCreated(BookingCreatedEvent event) {
        vehicleRepository.findById(event.vehicleId()).ifPresent(vehicle -> {
            vehicle.markReserved(event.startAt(), event.endAt());
            vehicleRepository.save(vehicle);
            LOGGER.info("Vehículo {} marcado como reservado por booking {}", vehicle.getId(), event.bookingId());
        });
    }

    @Override
    @Transactional
    public void handleBookingCancelled(BookingCancelledEvent event) {
        vehicleRepository.findById(event.vehicleId()).ifPresent(vehicle -> {
            vehicle.markAvailable();
            vehicleRepository.save(vehicle);
            LOGGER.info("Vehículo {} liberado tras cancelación del booking {}", vehicle.getId(), event.bookingId());
        });
    }

    @Override
    @Transactional
    public void handleRentalStarted(RentalStartedEvent event) {
        vehicleRepository.findById(event.vehicleId()).ifPresent(vehicle -> {
            vehicle.markInService();
            vehicleRepository.save(vehicle);
            LOGGER.info("Vehículo {} pasó a IN_SERVICE por rental {}", vehicle.getId(), event.bookingId());
        });
    }

    @Override
    @Transactional
    public void handleRentalFinished(RentalFinishedEvent event) {
        vehicleRepository.findById(event.vehicleId()).ifPresent(vehicle -> {
            vehicle.markFinished();
            vehicleRepository.save(vehicle);
            LOGGER.info("Vehículo {} disponible tras finalizar rental {}", vehicle.getId(), event.bookingId());
        });
    }
}
