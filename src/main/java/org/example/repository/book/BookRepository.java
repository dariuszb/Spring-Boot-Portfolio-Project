package org.example.repository.book;

import java.util.List;
import org.example.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>,
        JpaSpecificationExecutor<Book> {

    @Query(value = "SELECT DISTINCT b FROM Book b JOIN b.categories c "
            + "WHERE c.id = :categoryId")
    List<Book> findAllByCategoryId(Long categoryId);

}
