package org.example.service.bookservice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.dto.bookdto.BookDto;
import org.example.dto.bookdto.BookSearchParametersDto;
import org.example.dto.bookdto.CreateBookRequestDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.BookMapper;
import org.example.model.Book;
import org.example.model.Category;
import org.example.repository.book.BookRepository;
import org.example.repository.category.CategoryRepository;
import org.example.repository.user.SpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final SpecificationBuilder<Book> bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto createBook(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toEntity(createBookRequestDto);

        Set<Category> collect = fromIdToCategory(
                createBookRequestDto.categoriesIds());
        book.setCategories(collect);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        List<BookDto> list = bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
        return new PageImpl<>(list, pageable, bookRepository.count());
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
    public BookDto updateBookById(Long id, CreateBookRequestDto updateBookRequestDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + id + " not found"));
        book.setTitle(updateBookRequestDto.title());
        book.setAuthor(updateBookRequestDto.author());
        book.setPrice(updateBookRequestDto.price());
        book.setIsbn(updateBookRequestDto.isbn());

        Set<Category> collect = fromIdToCategory(
                updateBookRequestDto.categoriesIds());

        book.setCategories(collect);
        book.setDescription(updateBookRequestDto.description());
        book.setCoverImage(updateBookRequestDto.coverImage());
        Book saved = bookRepository.save(book);
        return bookMapper.toDto(saved);

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

    private Set<Category> fromIdToCategory(Set<Long> ids) {
        return ids.stream()
                .map(e -> categoryRepository.findById(e).orElseThrow(
                        () -> new EntityNotFoundException("Category with "
                        + "id " + e + "doesn't exist")))
                .collect(Collectors.toSet());
    }
}
