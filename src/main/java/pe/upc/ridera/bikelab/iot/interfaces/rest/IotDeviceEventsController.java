package pe.upc.ridera.bikelab.iot.interfaces.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import pe.upc.ridera.bikelab.configuration.OpenApiConfig;
import pe.upc.ridera.bikelab.iot.application.internal.IotDeviceEdgeStateService;
import pe.upc.ridera.bikelab.iot.application.dto.IotDeviceEventResource;
import pe.upc.ridera.bikelab.iot.application.services.IotDeviceEventService;
import pe.upc.ridera.bikelab.iot.interfaces.rest.resources.CreateIotDeviceEventRequest;

@RestController
@RequestMapping("/api/iot/events")
@Validated
@Tag(name = "BC: IoT")
public class IotDeviceEventsController {

    private final IotDeviceEventService service;
    private final IotDeviceEdgeStateService edgeStateService;
    private final String gatewayKey;

    public IotDeviceEventsController(IotDeviceEventService service,
            IotDeviceEdgeStateService edgeStateService,
            @Value("${app.gateway.key:dev-gateway-key}") String gatewayKey) {
        this.service = service;
        this.edgeStateService = edgeStateService;
        this.gatewayKey = gatewayKey;
    }

    @Operation(summary = "Registrar evento enviado por el API Gateway")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IotDeviceEventResource create(
            @RequestHeader(name = "X-Gateway-Key", required = false) String providedGatewayKey,
            @Valid @RequestBody CreateIotDeviceEventRequest request) {
        ensureGatewayKey(providedGatewayKey);
        IotDeviceEventResource event = service.register(request.deviceId(), request.eventType(), request.blocked(),
                request.message(), request.occurredAt(), request.latitude(), request.longitude(), request.speedKmph(),
                request.insideGeofence(), request.lockState());
        edgeStateService.recordEvent(request);
        return event;
    }

    @Operation(summary = "Obtener el evento IoT mas reciente")
    @GetMapping("/latest")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    public ResponseEntity<IotDeviceEventResource> latest() {
        return service.latest()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Operation(summary = "Listar eventos IoT recientes")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    public List<IotDeviceEventResource> recent(@RequestParam(defaultValue = "20") int limit) {
        return service.recent(limit);
    }

    private void ensureGatewayKey(String providedGatewayKey) {
        if (providedGatewayKey == null || !providedGatewayKey.equals(gatewayKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Gateway key invalida");
        }
    }
}
