package org.example.service;

import java.util.List;
import org.example.dto.BookDto;
import org.example.dto.BookSearchParametersDto;
import org.example.dto.CreateBookRequestDto;

public interface BookService {

    BookDto createBook(CreateBookRequestDto createBookRequestDto);

    List<BookDto> getAll();

    BookDto getBookById(Long id);

    void deleteBookById(Long id);

    BookDto updateBookById(Long id, CreateBookRequestDto updateBookRequestDto);

    List<BookDto> search(BookSearchParametersDto searchParameters);
}
