package org.example.repository.user;

import java.util.Optional;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    @Query("FROM User user "
            + "left join fetch user.roles "
            + "WHERE user.email = :email")
    Optional<User> findByEmail(String email);
}
