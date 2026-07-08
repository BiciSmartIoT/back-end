package pe.upc.ridera.bikelab.iot.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AcknowledgeIotDeviceCommandRequest(
        @NotBlank @Size(max = 40) String status,
        @Size(max = 160) String message) {
}
