package pe.upc.ridera.bikelab.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        tags = {
                @Tag(name = "BC: IAM", description = "Maneja usuarios, credenciales y perfiles."),
                @Tag(name = "BC: Providing", description = "Administra el registro y soporte de proveedores."),
                @Tag(name = "BC: Vehicles", description = "Maneja los vehiculos y su disponibilidad."),
                @Tag(name = "BC: Renting", description = "Gestiona las reservas y alquileres."),
                @Tag(name = "BC: Payments", description = "Procesa metodos de pago, cargos y pagos a proveedores."),
                @Tag(name = "BC: Metrics", description = "Exposicion publica de estadisticas agregadas."),
                @Tag(name = "BC: IoT", description = "Recibe y expone eventos reales del circuito ESP32.")
        }
)
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de BiceSmartIoT")
                        .description("Documentacion oficial de los servicios de la plataforma BiceSmartIoT.")
                        .version("v1"))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
