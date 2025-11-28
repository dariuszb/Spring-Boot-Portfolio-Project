package org.example.repository.book;

import lombok.RequiredArgsConstructor;
import org.example.dto.BookSearchParameters;
import org.example.model.Book;
import org.example.repository.user.SpecificationBuilder;
import org.example.repository.user.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {

    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> buildSpecification(BookSearchParameters searchParameters) {

        Specification<Book> specification = Specification.unrestricted();

        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {

            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(searchParameters.titles()));
        }

        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {

            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(searchParameters.authors()));
        }

        if (searchParameters.isbn() != null && searchParameters.isbn().length > 0) {

            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider("isbn")
                    .getSpecification(searchParameters.isbn()));
        }

        return specification;
    }
}
