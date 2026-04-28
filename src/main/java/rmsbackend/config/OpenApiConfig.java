package rmsbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rmsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RMS Backend API")
                        .version("v1")
                        .description("API documentation for RMS Backend."));
    }
}
