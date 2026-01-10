package org.example.service.bookservice;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Transactional
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
        return new PageImpl<>(list);
    }

    @Override
    public BookDto getBookById(Long id) {
        isBookWithIdExist(id);
        Book book = bookRepository.findById(id).get();
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        isBookWithIdExist(id);
        bookRepository.deleteById(id);
    }

    @Transactional
    @Override
    public BookDto updateBookById(Long id, CreateBookRequestDto updateBookRequestDto) {
        isBookWithIdExist(id);
        Book bookToUpdate = bookRepository.findById(id).get();
        Book updatedBook = updateBook(bookToUpdate, updateBookRequestDto);
        return bookMapper.toDto(updatedBook);

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
        List<Category> foundCategories = categoryRepository.findAllById(ids);
        List<Long> listFromRepo = foundCategories.stream()
                .map(Category::getId).toList();
        List<Long> input = ids.stream().toList();
        List<Long> notFoundIds = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            if (!listFromRepo.contains(input.get(i))) {
                notFoundIds.add(input.get(i));
            }
        }
        if (notFoundIds.size() == 1) {
            throw new EntityNotFoundException(
                    "Category with id " + input.get(0) + " not found");
        }
        if (notFoundIds.size() > 1) {
            throw new EntityNotFoundException(
                    "Categories with ids " + notFoundIds + " don't exist");
        }
        return new HashSet<>(foundCategories);
    }

    private Book updateBook(Book book, CreateBookRequestDto createBookRequestDto) {
        book.setTitle(createBookRequestDto.title());
        book.setAuthor(createBookRequestDto.author());
        book.setIsbn(createBookRequestDto.isbn());
        book.setPrice(createBookRequestDto.price());
        book.setDescription(createBookRequestDto.description());
        book.setCoverImage(createBookRequestDto.coverImage());

        if (createBookRequestDto.categoriesIds().isEmpty()) {
            book.setCategories(Collections.emptySet());
        } else {
            Set<Category> collect = fromIdToCategory(
                    createBookRequestDto.categoriesIds());

            book.setCategories(collect);
        }
        return bookRepository.save(book);
    }

    private void isBookWithIdExist(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Book with id " + id + " not found");
        }
    }
}
