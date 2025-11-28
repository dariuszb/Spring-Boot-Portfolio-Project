package org.example.repository.book.specification;

import java.util.Arrays;
import org.example.model.Book;
import org.example.repository.user.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecification implements SpecificationProvider<Book> {

    static final String title = "title";

    @Override
    public String getKey() {
        return title;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get(title).in(Arrays.stream(params).toArray());
    }
}
