package com.peoplecore.config;

import com.peoplecore.module.Role;
import com.peoplecore.enums.RoleName;
import com.peoplecore.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {

        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder()
                    .name(RoleName.ADMIN)
                    .description("System administrator")
                    .build());

            roleRepository.save(Role.builder()
                    .name(RoleName.HR)
                    .description("HR manager")
                    .build());

            roleRepository.save(Role.builder()
                    .name(RoleName.MANAGER)
                    .description("Manager")
                    .build());

            roleRepository.save(Role.builder()
                    .name(RoleName.EMPLOYEE)
                    .description("Employee")
                    .build());
        }

        System.out.println("🚀 Roles initialized");
    }
}