package org.example.mappers;

import java.util.Set;
import java.util.stream.Collectors;
import org.example.configuration.MapperConfiguration;
import org.example.dto.bookdto.BookDto;
import org.example.dto.bookdto.BookDtoWithoutCategoryIds;
import org.example.dto.bookdto.CreateBookRequestDto;
import org.example.model.Book;
import org.example.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfiguration.class)
public interface BookMapper {

    Book toEntity(CreateBookRequestDto createBookRequestDto);

    BookDto toDto(Book entity);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping()
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        Set<Long> longSet = book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        bookDto.setCategoriesIds(longSet);
    }

}
