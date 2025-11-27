package org.example;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.example.model.Book;
import org.example.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@SpringBootApplication
public class SpringBootPortfolioProjectApplication {

    private final BookService bookService;

    public static void main(String[] args) {

        SpringApplication.run(SpringBootPortfolioProjectApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book newBook = new Book();
                newBook.setTitle("New Book");
                newBook.setAuthor("New Author");
                newBook.setPrice(BigDecimal.valueOf(200));
                newBook.setIsbn("123456789");
                newBook.setDescription("New Book Description");
                newBook.setCoverImage("New Book Cover Image");

                bookService.save(newBook);

                System.out.println(bookService.findAll());

            }
        };
    }
}
