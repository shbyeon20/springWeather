package zerobase.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.w3c.dom.DocumentType;

@Configuration
public class SwaggerConfig { // Removed @EnableSwagger2

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(apiInfo())
                .addServersItem(new Server().url("/")); // Optional: Set server URL
        // Other potential customizations
    }

    private Info apiInfo() {
        String description = "Welcome Log Company";
        return new Info()
                .title("SWAGGER TEST")
                .description(description)
                .version("1.0");
    }
}
