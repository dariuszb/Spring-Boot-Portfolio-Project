package org.example.service.categoryservice;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.example.dto.bookdto.BookDtoWithoutCategoryIds;
import org.example.dto.categorydto.CategoryDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.BookMapper;
import org.example.mappers.CategoryMapper;
import org.example.model.Book;
import org.example.model.Category;
import org.example.repository.book.BookRepository;
import org.example.repository.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    @Test
    @DisplayName("Check, that exist category with chosen id is returned.")
    void getById_existId_Success() {

        Category category = new Category();
        category.setId(1L);
        category.setName("test");
        category.setDescription("test");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("test");
        categoryDto.setDescription("test");

        Mockito.when(categoryRepository.existsById(1L)).thenReturn(true);
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto resultCategory = categoryServiceImpl.getById(1L);

        Mockito.verify(categoryRepository).existsById(any(Long.class));

        Mockito.verify(categoryRepository).findById(any(Long.class));

        Mockito.verify(categoryMapper).toDto(any(Category.class));

        Assertions.assertThat(categoryDto).isEqualTo(resultCategory);
    }

    @Test
    @DisplayName("Verify correctness of updating category with chosen id")
    void update_correctInputParams_returnUpdatedObject() {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("nameToUpdate");
        categoryDto.setDescription("descriptionToUpdate");

        Category category = new Category();
        category.setId(1L);
        category.setName("category1");
        category.setDescription("description1");

        Long categoryId = 2L;

        Category category2 = new Category();
        category2.setId(categoryId);
        category2.setName("category2");
        category2.setDescription("description2");

        Mockito.when(categoryRepository.existsById(categoryId))
                .thenReturn(true);

        Mockito.when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        Mockito.when(categoryRepository.save(category))
                .thenReturn(category2);

        Mockito.when(categoryMapper.toDto(category2))
                .thenReturn(categoryDto);

        CategoryDto updatedCategory =
                categoryServiceImpl.update(2L, categoryDto);

        Mockito.verify(categoryRepository)
                .existsById(any(Long.class));

        Mockito.verify(categoryRepository).save(any(Category.class));

        Mockito.verify(categoryMapper).toDto(any(Category.class));

        Assertions.assertThat(categoryDto).isEqualTo(updatedCategory);
    }

    @Test
    @DisplayName("Verify the correct books "
            + "with chosen category id are returned")
    void getBookWithoutCategories_correctInputArguments_returnListWithBooks() {

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("category1");
        category1.setDescription("description2");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("category2");
        category2.setDescription("description2");

        Book book1 = new Book();
        book1.setId(1L)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of(category1))
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        BookDtoWithoutCategoryIds bookDto1 = new BookDtoWithoutCategoryIds();
        bookDto1.setId(1L)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        Book book2 = new Book();
        book2.setId(2L)
                .setTitle("Title2")
                .setAuthor("Author2")
                .setIsbn("123456788")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of(category2))
                .setDescription("Description2")
                .setCoverImage("CoverImage2");

        BookDtoWithoutCategoryIds bookDto2 = new BookDtoWithoutCategoryIds();
        bookDto2.setId(2L)
                .setTitle("Title2")
                .setAuthor("Author2")
                .setIsbn("123456788")
                .setPrice(BigDecimal.valueOf(100))
                .setDescription("Description2")
                .setCoverImage("CoverImage2");

        Mockito.when(categoryRepository.existsById(1L)).thenReturn(true);
        Mockito.when(categoryRepository.existsById(2L)).thenReturn(true);
        Mockito.when(bookRepository.findAllByCategoryId(1L)).thenReturn(
                List.of(book1));
        Mockito.when(bookMapper.toDtoWithoutCategories(book1)).thenReturn(
                bookDto1);

        Mockito.when(bookRepository.findAllByCategoryId(2L)).thenReturn(
                List.of(book2));
        Mockito.when(bookMapper.toDtoWithoutCategories(book2)).thenReturn(
                bookDto2);

        List<BookDtoWithoutCategoryIds> bookWithoutCategories1 =
                categoryServiceImpl.getBookWithoutCategories(1L);

        Assertions.assertThat(bookWithoutCategories1).isEqualTo(List.of(bookDto1));

        List<BookDtoWithoutCategoryIds> bookWithoutCategories2 =
                categoryServiceImpl.getBookWithoutCategories(2L);

        Mockito.verify(categoryRepository, Mockito.times(2))
                .existsById(any(Long.class));

        Mockito.verify(bookRepository, Mockito.times(2))
                .findAllByCategoryId(any(Long.class));

        Mockito.verify(bookMapper, Mockito.times(2))
                .toDtoWithoutCategories(any(Book.class));

        Assertions.assertThat(bookWithoutCategories2).isEqualTo(List.of(bookDto2));

    }

    @Test
    @DisplayName("Check, that exception is throwing "
            + "when category with input category id doesn't exist")
    void getBookWithoutCategories_nonExistingCategory_thrownException() {

        Mockito.when(categoryRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> categoryServiceImpl.getById(2L));

        String expectedMessage = "Category with id 2 doesn't exist";

        Mockito.verify(categoryRepository)
                .existsById(any(Long.class));

        Assertions.assertThat(expectedMessage).isEqualTo(exception.getMessage());

    }

    @Test
    @DisplayName("Verify the books are returned "
            + "when the DB is not empty")
    void findAll_nonEmptyDB_returnAllInputsFromDB() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("category1");
        category1.setDescription("description1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("category2");
        category2.setDescription("description2");

        CategoryDto categoryDto1 = new CategoryDto();
        categoryDto1.setId(1L);
        categoryDto1.setName("categoryDto1");
        categoryDto1.setDescription("descriptionDto1");

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setName("categoryDto2");
        categoryDto2.setDescription("descriptionDto2");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category1);
        categoryList.add(category2);

        Mockito.when(categoryRepository.findAll(Pageable.unpaged()))
                .thenReturn(new PageImpl<Category>(categoryList));

        Mockito.when(categoryMapper.toDto(category1))
                .thenReturn(categoryDto1);
        Mockito.when(categoryMapper.toDto(category2))
                .thenReturn(categoryDto2);

        Page<CategoryDto> all = categoryServiceImpl.findAll(Pageable.unpaged());

        List<CategoryDto> allList = all.stream().toList();

        Mockito.verify(categoryRepository).findAll(Pageable.unpaged());

        Mockito.verify(categoryMapper, Mockito.times(2))
                .toDto(any(Category.class));

        Assertions.assertThat(all.getTotalElements()).isEqualTo(2);

        Assertions.assertThat(categoryMapper.toDto(category1))
                .isEqualTo(allList.get(0));
        Assertions.assertThat(categoryMapper.toDto(category2))
                .isEqualTo(allList.get(1));

    }
}
