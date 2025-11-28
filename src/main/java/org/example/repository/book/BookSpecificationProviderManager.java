package org.example.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.model.Book;
import org.example.repository.user.SpecificationProvider;
import org.example.repository.user.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {

    private final List<SpecificationProvider<Book>> specificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return specificationProviders
                .stream()
                .filter(b -> b.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Specification Provider not found for key " + key));
    }
}
