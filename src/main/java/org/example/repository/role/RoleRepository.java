package org.example.repository.role;

import java.util.Optional;
import org.example.model.Role;
import org.example.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository
        extends JpaRepository<Role,Long> {

    Optional<Role> findByName(RoleName name);
}
