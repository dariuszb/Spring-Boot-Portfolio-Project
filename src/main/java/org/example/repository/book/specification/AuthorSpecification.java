package org.example.repository.book.specification;

import java.util.Arrays;
import org.example.model.Book;
import org.example.repository.user.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecification implements SpecificationProvider<Book> {

    static final String author = "author";

    @Override
    public String getKey() {
        return author;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get(author).in(Arrays.stream(params).toArray());
    }
}
