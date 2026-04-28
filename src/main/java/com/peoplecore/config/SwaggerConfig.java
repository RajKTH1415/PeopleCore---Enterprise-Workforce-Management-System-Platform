package com.peoplecore.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI peopleCoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🏆 PeopleCore -  Enterprise Workforce Management Platform")
                        .description("""
                                PeopleCore is a scalable enterprise-grade workforce management platform
                                designed to streamline employee lifecycle, organizational hierarchy,
                                skills, certifications, payroll integration, and workforce analytics and many more.
                                
                                It handles:
                                - Employee lifecycle (Onboarding → Probation → Confirmation → Exit)
                                - Department & hierarchy management
                                - Skills & certifications tracking
                                - Role-based access control (RBAC)
                                - Audit logs & soft delete support
                                
                                Built using Spring Boot 3, Java 21, PostgreSQL, JWT Security.
                                Designed for real-world HRMS and enterprise systems.
                                """)
                        .version("v1.0")
                        .contact(new Contact()
                                .name("PeopleCore Team")
                                .email("support@peoplecore.com")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
