package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.BookDto;
import org.example.dto.CreateBookRequestDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.BookMapper;
import org.example.model.Book;
import org.example.repository.BookRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.dtoToModel(createBookRequestDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.modelToDto(savedBook);
    }

    @Override
    public List<BookDto> getAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::modelToDto)
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.getById(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + id + " not found"));
        return bookMapper.modelToDto(book);
    }
}
