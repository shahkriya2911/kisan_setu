package com.project.kisan_setu.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi(){
        final String securitySchemaName = "cookieAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Kisan_Setu")
                        .version("1.0.0")
                        .description("Kisan_Setu is an agri-tech project")
                        .contact(new Contact()
                                .name("Kisan_Setu team")
                                .email("kisan_setu_support@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemaName))
                .components(new Components().addSecuritySchemes(
                        securitySchemaName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .name("accessToken")
                                .description("JWT access token is stored in httpOnly cookie")
                ));
    }
}
