package org.example.repository.book;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.dto.BookSearchParametersDto;
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
    public Specification<Book> buildSpecification(BookSearchParametersDto searchParameters) {

        Specification<Book> specification = Specification.unrestricted();

        Map<String, String[]> parametersMap = new HashMap<>();
        parametersMap.put("title", searchParameters.titles());
        parametersMap.put("author", searchParameters.authors());
        parametersMap.put("isbn", searchParameters.isbn());

        for (Map.Entry<String, String[]> entry : parametersMap.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length > 0) {

                specification = specification.and(specificationProviderManager
                        .getSpecificationProvider(entry.getKey())
                        .getSpecification(entry.getValue()));
            }
        }
        return specification;
    }
}
