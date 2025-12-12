package com.bcafinance.training.controller;

import com.bcafinance.training.entity.Permission;
import com.bcafinance.training.entity.Role;
import com.bcafinance.training.entity.User;
import com.bcafinance.training.repository.PermissionRepository;
import com.bcafinance.training.repository.RoleRepository;
import com.bcafinance.training.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('SUPERADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    // User Management
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // Role Management
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    // Permission Management
    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        Permission permission = Permission.builder().name(name).build();
        return ResponseEntity.ok(permissionRepository.save(permission));
    }

    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<Role> addPermissionToRole(@PathVariable Long roleId, @RequestBody Map<String, Long> request) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        Long permissionId = request.get("permissionId");
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        role.getPermissions().add(permission);
        return ResponseEntity.ok(roleRepository.save(role));
    }
}
