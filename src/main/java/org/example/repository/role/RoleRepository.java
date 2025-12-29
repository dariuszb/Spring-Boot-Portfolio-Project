package org.example.repository.role;

import org.example.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository
        extends JpaRepository<Role,Long> {

    @Query(value = "SELECT * FROM roles WHERE role = :role", nativeQuery = true)
    Role findByName(String role);
}
