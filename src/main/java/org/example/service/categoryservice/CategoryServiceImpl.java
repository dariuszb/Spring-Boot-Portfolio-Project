package org.example.service.categoryservice;

import jakarta.transaction.Transactional;
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
        return new PageImpl<>(list);
    }

    @Override
    public CategoryDto getById(Long id) {
        isCategoryWithIdExist(id);
        Category category = categoryRepository.findById(id).get();
        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public CategoryDto save(CreateCategoryDto createCategoryDto) {
        return categoryMapper.toDto(categoryRepository.save(
                categoryMapper.toEntity(createCategoryDto)));
    }

    @Transactional
    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        isCategoryWithIdExist(id);
        Category updatedCategory = updateCategory(id, categoryDto);
        return categoryMapper.toDto(updatedCategory);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        isCategoryWithIdExist(id);
        categoryRepository.deleteById(id);
    }

    @Override
    public List<BookDtoWithoutCategoryIds> getBookWithoutCategories(Long id) {
        isCategoryWithIdExist(id);
        return bookRepository.findAllByCategoryId(id).stream()
                    .map(bookMapper::toDtoWithoutCategories)
                    .toList();

    }

    private Category updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).get();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        return categoryRepository.save(category);
    }

    private void isCategoryWithIdExist(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Category with id " + id + " doesn't exist");
        }
    }

}
