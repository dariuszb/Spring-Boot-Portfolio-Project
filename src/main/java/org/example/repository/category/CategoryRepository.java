package org.example.repository.category;

import java.util.Optional;
import org.example.model.Book;
import org.example.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<Category, Long>,
        JpaSpecificationExecutor<Book> {
    Optional<Category> findById(Long id);
}
