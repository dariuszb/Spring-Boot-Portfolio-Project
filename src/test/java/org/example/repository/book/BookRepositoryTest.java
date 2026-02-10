package org.example.repository.book;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.example.model.Book;
import org.example.model.Category;
import org.example.repository.category.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = AFTER_CLASS)
@Sql(scripts = "classpath:database/delete-all-books.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Find all books with input category id")
    @Sql(scripts = "classpath:database/clear-all-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllBooksWithInputCategoriesId_correctInputData_success() {

        Category category1 = new Category();
        category1.setName("Category 1");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Category 2");
        categoryRepository.save(category2);

        Book book = new Book();
        book.setTitle("Title")
                .setAuthor("Author")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of(category1));

        bookRepository.save(book);

        Book book2 = new Book();
        book2.setTitle("Title2")
                .setAuthor("Author2")
                .setIsbn("123456788")
                .setPrice(BigDecimal.valueOf(200))
                .setCategories(Set.of(category2));

        bookRepository.save(book2);

        Assertions.assertEquals(List.of(book),
                bookRepository.findAllByCategoryId(category1.getId()));

        Assertions.assertEquals(List.of(book2),
                bookRepository.findAllByCategoryId(category2.getId()));
    }

}
