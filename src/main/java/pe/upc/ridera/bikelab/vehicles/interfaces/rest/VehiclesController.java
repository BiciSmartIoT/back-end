package pe.upc.ridera.bikelab.vehicles.interfaces.rest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import pe.upc.ridera.bikelab.configuration.OpenApiConfig;
import pe.upc.ridera.bikelab.providing.application.services.ProviderApprovalService;
import pe.upc.ridera.bikelab.vehicles.application.commands.BlockAvailabilityCommand;
import pe.upc.ridera.bikelab.vehicles.application.commands.CreateVehicleCommand;
import pe.upc.ridera.bikelab.vehicles.application.commands.UnblockAvailabilityCommand;
import pe.upc.ridera.bikelab.vehicles.application.commands.UpdateVehicleCommand;
import pe.upc.ridera.bikelab.vehicles.application.dto.AvailabilitySlotResource;
import pe.upc.ridera.bikelab.vehicles.application.dto.VehicleResource;
import pe.upc.ridera.bikelab.vehicles.application.services.VehicleCommandService;
import pe.upc.ridera.bikelab.vehicles.application.services.VehicleQueryService;
import pe.upc.ridera.bikelab.vehicles.interfaces.rest.resources.BlockAvailabilityRequest;
import pe.upc.ridera.bikelab.vehicles.interfaces.rest.resources.CreateVehicleRequest;
import pe.upc.ridera.bikelab.vehicles.interfaces.rest.resources.UpdateVehicleRequest;

/**
 * Esta clase implementa el API REST para que los proveedores administren su flota de vehículos.
 */ 
@RestController
@RequestMapping("/api/vehicles")
@Validated
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
@Tag(name = "BC: Vehicles")
public class VehiclesController {

    private final VehicleCommandService vehicleCommandService;
    private final VehicleQueryService vehicleQueryService;
    private final ProviderApprovalService providerApprovalService;

    public VehiclesController(VehicleCommandService vehicleCommandService,
            VehicleQueryService vehicleQueryService,
            ProviderApprovalService providerApprovalService) {
        this.vehicleCommandService = vehicleCommandService;
        this.vehicleQueryService = vehicleQueryService;
        this.providerApprovalService = providerApprovalService;
    }

    @Operation(summary = "Registrar un nuevo vehículo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_PROVIDER')")
    public VehicleResource create(@Valid @RequestBody CreateVehicleRequest request) {
        UUID ownerId = currentUserUuid();
        providerApprovalService.ensureProviderApproved(ownerId);
        var command = new CreateVehicleCommand(ownerId, request.title(), request.description(), request.hourlyPrice(),
                request.latitude(), request.longitude(), request.deviceId());
        return vehicleCommandService.createVehicle(command);
    }

    @Operation(summary = "Actualizar un vehículo existente")
    @PatchMapping("/{vehicleId}")
    @PreAuthorize("hasAnyAuthority('ROLE_PROVIDER','ROLE_ADMIN')")
    public VehicleResource update(@PathVariable UUID vehicleId, @Valid @RequestBody UpdateVehicleRequest request) {
        Authentication authentication = authentication();
        UUID requesterId = currentUserUuid(authentication);
        boolean overrideOwnership = hasRole(authentication, "ROLE_ADMIN");
        if (!overrideOwnership) {
            providerApprovalService.ensureProviderApproved(requesterId);
        }
        var command = new UpdateVehicleCommand(requesterId, vehicleId, request.title(), request.description(),
                request.hourlyPrice(), request.latitude(), request.longitude(), request.deviceId(),
                request.desiredStatus(), overrideOwnership);
        return vehicleCommandService.updateVehicle(command);
    }

    @Operation(summary = "Bloquear disponibilidad de un vehículo")
    @PostMapping("/{vehicleId}/availability/block")
    @PreAuthorize("hasAuthority('ROLE_PROVIDER')")
    public AvailabilitySlotResource block(@PathVariable UUID vehicleId,
            @Valid @RequestBody BlockAvailabilityRequest request) {
        UUID requesterId = currentUserUuid();
        providerApprovalService.ensureProviderApproved(requesterId);
        var command = new BlockAvailabilityCommand(requesterId, vehicleId, request.startAt(), request.endAt());
        return vehicleCommandService.blockAvailability(command);
    }

    @Operation(summary = "Liberar un bloqueo de disponibilidad")
    @PostMapping("/{vehicleId}/availability/unblock")
    @PreAuthorize("hasAuthority('ROLE_PROVIDER')")
    public ResponseEntity<Void> unblock(@PathVariable UUID vehicleId, @RequestParam UUID slotId) {
        UUID requesterId = currentUserUuid();
        providerApprovalService.ensureProviderApproved(requesterId);
        vehicleCommandService.unblockAvailability(new UnblockAvailabilityCommand(requesterId, vehicleId, slotId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener un vehículo por su identificador")
    @GetMapping("/{vehicleId}")
    public VehicleResource get(@PathVariable UUID vehicleId) {
        return vehicleQueryService.getVehicle(vehicleId);
    }

    @Operation(summary = "Listar todos los vehículos disponibles")
    @GetMapping
    public List<VehicleResource> list() {
        return vehicleQueryService.listAll();
    }

    @Operation(summary = "Listar los vehículos del proveedor autenticado")
    @GetMapping("/own")
    @PreAuthorize("hasAuthority('ROLE_PROVIDER')")
    public List<VehicleResource> own() {
        UUID ownerId = currentUserUuid();
        providerApprovalService.ensureProviderApproved(ownerId);
        return vehicleQueryService.getOwnVehicles(ownerId);
    }

    @Operation(summary = "Buscar vehículos cercanos a una ubicación")
    @GetMapping("/search")
    public List<VehicleResource> search(@RequestParam double lat, @RequestParam double lng,
            @RequestParam double radius) {
        return vehicleQueryService.searchNearby(lat, lng, radius);
    }

    private UUID currentUserUuid() {
        return currentUserUuid(authentication());
    }

    private UUID currentUserUuid(Authentication authentication) {
        String principal = authentication.getName();
        return UUID.nameUUIDFromBytes(principal.getBytes(StandardCharsets.UTF_8));
    }

    private Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }
}
