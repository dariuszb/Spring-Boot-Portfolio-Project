package org.example.mappers;

import org.example.dto.BookDto;
import org.example.dto.CreateBookRequestDto;
import org.example.mapperconfiguration.MapperConfiguration;
import org.example.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface BookMapper {

    Book dtoToModel(CreateBookRequestDto createBookRequestDto);

    BookDto modelToDto(Book book);

}
