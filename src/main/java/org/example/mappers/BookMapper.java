package org.example.mappers;

import org.example.dto.bookdto.BookDto;
import org.example.dto.bookdto.CreateBookRequestDto;
import org.example.mapperconfiguration.MapperConfiguration;
import org.example.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface BookMapper {

    Book toEntity(CreateBookRequestDto createBookRequestDto);

    BookDto toDto(Book entity);

}
