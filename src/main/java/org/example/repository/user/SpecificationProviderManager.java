package org.example.repository.user;

public interface SpecificationProviderManager<T> {

    SpecificationProvider<T> getSpecificationProvider(String key);
}
