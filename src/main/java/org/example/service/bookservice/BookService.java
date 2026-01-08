package org.example.service.bookservice;

import java.util.List;
import org.example.dto.bookdto.BookDto;
import org.example.dto.bookdto.BookSearchParametersDto;
import org.example.dto.bookdto.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookDto createBook(CreateBookRequestDto createBookRequestDto);

    Page<BookDto> findAll(Pageable pageable);

    BookDto getBookById(Long id);

    void deleteBookById(Long id);

    CreateBookRequestDto updateBookById(Long id, CreateBookRequestDto updateBookRequestDto);

    List<BookDto> search(BookSearchParametersDto searchParameters);
}
