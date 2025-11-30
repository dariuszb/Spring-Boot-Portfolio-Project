package org.example.service;

import java.util.List;
import org.example.dto.BookDto;
import org.example.dto.BookSearchParametersDto;
import org.example.dto.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookDto createBook(CreateBookRequestDto createBookRequestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto getBookById(Long id);

    void deleteBookById(Long id);

    CreateBookRequestDto updateBookById(Long id, CreateBookRequestDto updateBookRequestDto);

    List<BookDto> search(BookSearchParametersDto searchParameters);
}
