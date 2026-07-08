package pe.upc.ridera.bikelab.iot.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateIotDeviceCommandRequest(
        @NotBlank @Pattern(regexp = "LOCK|UNLOCK|RESET|SET_CONFIG") String type,
        @NotBlank @Size(max = 160) String reason) {
}
