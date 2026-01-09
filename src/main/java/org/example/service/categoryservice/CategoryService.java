package org.example.service.categoryservice;

import java.util.List;
import org.example.dto.bookdto.BookDtoWithoutCategoryIds;
import org.example.dto.categorydto.CategoryDto;
import org.example.dto.categorydto.CreateCategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    Page<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CreateCategoryDto createCategoryDto);

    CategoryDto update(Long id, CategoryDto categoryDto);

    void deleteById(Long id);

    List<BookDtoWithoutCategoryIds> getBookWithoutCategories(Long id);
}
