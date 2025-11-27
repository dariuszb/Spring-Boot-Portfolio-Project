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
        Book book = bookMapper.toEntity(createBookRequestDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Override
    public List<BookDto> getAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + id + " not found"));
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public CreateBookRequestDto updateBookById(Long id, CreateBookRequestDto updateBookRequestDto) {
        Book book = bookRepository.findById(id).get();
        book.setTitle(updateBookRequestDto.title());
        book.setAuthor(updateBookRequestDto.author());
        book.setPrice(updateBookRequestDto.price());
        book.setIsbn(updateBookRequestDto.isbn());
        book.setDescription(updateBookRequestDto.description());
        book.setCoverImage(updateBookRequestDto.coverImage());
        bookRepository.save(book);
        return updateBookRequestDto;

    }
}
