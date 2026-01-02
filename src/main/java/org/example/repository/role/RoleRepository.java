package org.example.repository.role;

import java.util.Optional;
import org.example.model.Book;
import org.example.model.Role;
import org.example.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleRepository
        extends JpaRepository<Role,Long>, JpaSpecificationExecutor<Book> {

    Optional<Role> findByName(RoleName name);
}
