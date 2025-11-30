package org.example.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.BookDto;
import org.example.dto.BookSearchParametersDto;
import org.example.dto.CreateBookRequestDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.BookMapper;
import org.example.model.Book;
import org.example.repository.book.BookRepository;
import org.example.repository.user.SpecificationBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final SpecificationBuilder<Book> bookSpecificationBuilder;

    @Override
    public BookDto createBook(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toEntity(createBookRequestDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
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
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + id + " not found"));
        book.setTitle(updateBookRequestDto.title());
        book.setAuthor(updateBookRequestDto.author());
        book.setPrice(updateBookRequestDto.price());
        book.setIsbn(updateBookRequestDto.isbn());
        book.setDescription(updateBookRequestDto.description());
        book.setCoverImage(updateBookRequestDto.coverImage());
        bookRepository.save(book);
        return updateBookRequestDto;

    }

    @Override
    public List<BookDto> search(BookSearchParametersDto params) {
        Specification<Book> buildSpecification = bookSpecificationBuilder
                .buildSpecification(params);
        return bookRepository.findAll(buildSpecification)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
