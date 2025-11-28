package org.example.repository.user;

import org.example.dto.BookSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> buildSpecification(BookSearchParameters searchParameters);
}
