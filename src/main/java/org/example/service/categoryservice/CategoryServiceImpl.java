package org.example.service.categoryservice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.bookdto.BookDtoWithoutCategoryIds;
import org.example.dto.categorydto.CategoryDto;
import org.example.dto.categorydto.CreateCategoryDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.BookMapper;
import org.example.mappers.CategoryMapper;
import org.example.model.Category;
import org.example.repository.book.BookRepository;
import org.example.repository.category.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public Page<CategoryDto> findAll(Pageable pageable) {
        List<CategoryDto> list = categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
        return new PageImpl<>(list, pageable, categoryRepository.count());
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Category with id " + id + " doesn't exist"));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto save(CreateCategoryDto createCategoryDto) {
        return categoryMapper.toDto(categoryRepository.save(
                categoryMapper.toEntity(createCategoryDto)));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(
                    "Category with id " + id + " doesn't exist");
        }
        Category updatedCategory = updateCategory(id, categoryDto);
        Category updatedCategoryResponse =
                    categoryRepository.findById(updatedCategory.getId()).get();
        return categoryMapper.toDto(updatedCategoryResponse);

    }

    @Override
    public void deleteById(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(
                    "Category with id " + id + " doesn't exist");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<BookDtoWithoutCategoryIds> getBookWithoutCategories(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(
                    "Category with id " + id + " doesn't exist");
        }
        return bookRepository.findAllByCategoryId(id).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    private Category updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).get();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        categoryRepository.save(category);
        return category;
    }

}
