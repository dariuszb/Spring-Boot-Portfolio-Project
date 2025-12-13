package org.example.repository.user;

import org.example.dto.bookdto.BookSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> buildSpecification(BookSearchParametersDto searchParameters);
}
