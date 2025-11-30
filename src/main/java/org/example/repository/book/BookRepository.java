package org.example.repository.book;

import org.example.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends PagingAndSortingRepository
        <Book, Long>, JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

}
