package com.bcafinance.training.config;

import com.bcafinance.training.entity.ERole;
import com.bcafinance.training.entity.Plafond;
import com.bcafinance.training.entity.Role;
import com.bcafinance.training.entity.User;
import com.bcafinance.training.repository.PlafondRepository;
import com.bcafinance.training.repository.RoleRepository;
import com.bcafinance.training.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PlafondRepository plafondRepository;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Init Roles
        for (ERole roleName : ERole.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
            }
        }

        // Init Super Admin
        if (!userRepository.existsByUsername("admin")) {
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(ERole.SUPERADMIN).ifPresent(roles::add);
            roleRepository.findByName(ERole.MARKETING).ifPresent(roles::add);
            roleRepository.findByName(ERole.BRANCH_MANAGER).ifPresent(roles::add);
            roleRepository.findByName(ERole.BACK_OFFICE).ifPresent(roles::add);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@bcafinance.co.id")
                    .password(encoder.encode("Admin123!"))
                    .fullName("Super Admin")
                    .roles(roles)
                    .build();
            userRepository.save(admin);
        }

        // Init Sample Plafonds
        if (plafondRepository.count() == 0) {
            plafondRepository.save(Plafond.builder()
                    .name("Kredit Motor")
                    .description("Untuk pembelian sepeda motor baru")
                    .type("VEHICLE")
                    .maxAmount(new BigDecimal("50000000"))
                    .interestRate(new BigDecimal("5.5"))
                    .build());

            plafondRepository.save(Plafond.builder()
                    .name("Kredit Mobil")
                    .description("Untuk pembelian mobil baru")
                    .type("VEHICLE")
                    .maxAmount(new BigDecimal("500000000"))
                    .interestRate(new BigDecimal("4.5"))
                    .build());
        }
    }
}
