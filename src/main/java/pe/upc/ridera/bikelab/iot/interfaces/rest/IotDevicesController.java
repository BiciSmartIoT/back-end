package pe.upc.ridera.bikelab.iot.interfaces.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import pe.upc.ridera.bikelab.configuration.OpenApiConfig;
import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceCommandResource;
import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceConfigResource;
import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceStateResource;
import pe.upc.ridera.bikelab.iot.application.internal.IotDeviceEdgeStateService;
import pe.upc.ridera.bikelab.iot.interfaces.rest.resources.AcknowledgeIotDeviceCommandRequest;
import pe.upc.ridera.bikelab.iot.interfaces.rest.resources.CreateIotDeviceCommandRequest;
import pe.upc.ridera.bikelab.iot.interfaces.rest.resources.UpdateIotDeviceConfigRequest;

@RestController
@RequestMapping("/api/iot/devices")
@Validated
@Tag(name = "BC: IoT Devices")
public class IotDevicesController {

    private final IotDeviceEdgeStateService edgeStateService;
    private final String gatewayKey;

    public IotDevicesController(IotDeviceEdgeStateService edgeStateService,
            @Value("${app.gateway.key:dev-gateway-key}") String gatewayKey) {
        this.edgeStateService = edgeStateService;
        this.gatewayKey = gatewayKey;
    }

    @Operation(summary = "Obtener configuracion IoT para polling del edge")
    @GetMapping("/{deviceId}/config")
    public IotDeviceConfigResource configForDevice(
            @PathVariable String deviceId,
            @RequestHeader(name = "X-Gateway-Key", required = false) String providedGatewayKey) {
        ensureGatewayKeyOrAuthenticated(providedGatewayKey);
        return edgeStateService.getConfig(deviceId);
    }

    @Operation(summary = "Actualizar configuracion IoT desde la app")
    @PutMapping("/{deviceId}/config")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    public IotDeviceConfigResource updateConfig(
            @PathVariable String deviceId,
            @Valid @RequestBody UpdateIotDeviceConfigRequest request) {
        return edgeStateService.updateConfig(deviceId, request);
    }

    @Operation(summary = "Consultar estado IoT mas reciente")
    @GetMapping("/{deviceId}/state")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    public ResponseEntity<IotDeviceStateResource> state(@PathVariable String deviceId) {
        return edgeStateService.state(deviceId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Crear comando IoT desde la app")
    @PostMapping("/{deviceId}/commands")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    public IotDeviceCommandResource createCommand(
            @PathVariable String deviceId,
            @Valid @RequestBody CreateIotDeviceCommandRequest request) {
        return edgeStateService.enqueueCommand(deviceId, request);
    }

    @Operation(summary = "Obtener el siguiente comando pendiente para el dispositivo")
    @GetMapping("/{deviceId}/commands/next")
    public ResponseEntity<IotDeviceCommandResource> nextCommand(
            @PathVariable String deviceId,
            @RequestHeader(name = "X-Gateway-Key", required = false) String providedGatewayKey) {
        ensureGatewayKey(providedGatewayKey);
        return edgeStateService.nextCommand(deviceId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Confirmar ejecucion de comando por el dispositivo")
    @PostMapping("/{deviceId}/commands/{commandId}/ack")
    public IotDeviceCommandResource acknowledgeCommand(
            @PathVariable String deviceId,
            @PathVariable UUID commandId,
            @RequestHeader(name = "X-Gateway-Key", required = false) String providedGatewayKey,
            @Valid @RequestBody AcknowledgeIotDeviceCommandRequest request) {
        ensureGatewayKey(providedGatewayKey);
        return edgeStateService.acknowledgeCommand(deviceId, commandId, request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comando IoT no encontrado"));
    }

    private void ensureGatewayKey(String providedGatewayKey) {
        if (providedGatewayKey == null || !providedGatewayKey.equals(gatewayKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Gateway key invalida");
        }
    }

    private void ensureGatewayKeyOrAuthenticated(String providedGatewayKey) {
        if (providedGatewayKey != null && providedGatewayKey.equals(gatewayKey)) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Gateway key o JWT requerido");
    }
}
