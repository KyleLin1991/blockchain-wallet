package tw.com.kyle.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kyle
 * @since 2025/2/25
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "BlockChain Wallet"), servers = {@Server(url = "/", description = "Server URL")})
public class SwaggerConfig {

    @Bean
    public OpenAPI baseOpenApi() {
        String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .scheme("bearer")
                                        .bearerFormat("jwt")
                                        .type(SecurityScheme.Type.HTTP)
                                        .in(SecurityScheme.In.HEADER)
                        )
                );

    }
}
