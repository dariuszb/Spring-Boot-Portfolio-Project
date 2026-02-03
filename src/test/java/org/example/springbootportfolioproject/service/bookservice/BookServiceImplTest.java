package org.example.springbootportfolioproject.service.bookservice;

import org.assertj.core.api.Assertions;
import org.example.dto.bookdto.BookDto;
import org.example.dto.bookdto.CreateBookRequestDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.mappers.BookMapper;
import org.example.model.Book;
import org.example.model.Category;
import org.example.repository.book.BookRepository;
import org.example.repository.category.CategoryRepository;
import org.example.service.bookservice.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository; // umożliwia pozbycia się zależnosći zenętrznych

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName(
            "Varify correctness of creation new book")
    public void createNewBook_correctInputsArguments_Ok() {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto(
                "Title1", "Author1", "897564231",
                 BigDecimal.valueOf(100), Set.of(), "Description1",
                "CoverImage1");
        Book book = new Book();
        book.setTitle("Title1").setAuthor("Author1")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of())
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        BookDto result = new BookDto();

        result.setTitle("Title1").setAuthor("Author1")
                .setPrice(BigDecimal.valueOf(100))
                .setCategoriesIds(Set.of())
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        Mockito.when(bookMapper.toEntity(createBookRequestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(result);

        BookDto savedDto = bookService.createBook(createBookRequestDto);

        Assertions.assertThat(savedDto).isEqualTo(result);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
        Mockito.verifyNoMoreInteractions(bookRepository, bookMapper);

    }

    @Test
    @DisplayName("Varify throwing exception"
            + " when category id is incorrect")
    public void createNewBook_invalidCategoryIdData_ThrowsException() {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto(
                "Title1", "Author1", "897564231",
                BigDecimal.valueOf(100), Set.of(5L), "Description1",
                "CoverImage1");

        EntityNotFoundException entityNotFoundException =
                assertThrows(EntityNotFoundException.class,
                    () -> bookService.createBook(createBookRequestDto));

        String exceptionMessage = "Category with id 5 not found";

        Assertions.assertThat(exceptionMessage)
                .isEqualTo(entityNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Varify correctness of get book with chosen id")
    public void getBookById_correctInputArguments_Ok() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Category1");

        Long bookId = 1L;

        Book book = new Book();
        book.setId(bookId)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of(category))
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        BookDto bookDto = new BookDto();
        bookDto.setId(bookId)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setCategoriesIds(Set.of(1L))
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        Mockito.when(bookRepository.existsById(1L)).thenReturn(true);
        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        Assertions.assertThat(
                bookService.getBookById(1L)).isEqualTo(bookDto);

    }

    @Test
    @DisplayName("Varify correctness of update book with chosen id")
    void updateBookById_correctInputArguments_ok() {
        Long bookId = 1L;

        CreateBookRequestDto newBookValues = new CreateBookRequestDto(
                "UpdatedTitle","UpdatedAuthor","123456789",
                BigDecimal.valueOf(100),
                Set.of(),
                "Description1",
                "CoverImage1");

        Book bookInDB = new Book();
        bookInDB.setId(bookId)
                .setTitle("Title")
                .setAuthor("Author")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of())
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        Book updatedBook = new Book();
        updatedBook.setId(bookId)
                .setTitle(newBookValues.title())
                .setAuthor(newBookValues.author())
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of())
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setTitle("UpdatedTitle")
                .setAuthor("UpdatedAuthor")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setCategoriesIds(Set.of())
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        Mockito.when(bookRepository.existsById(1L)).thenReturn(true);
        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(bookInDB));
        Mockito.when(bookRepository.save(bookInDB))
                .thenReturn(updatedBook);
        Mockito.when(bookMapper.toDto(updatedBook))
                .thenReturn(updatedBookDto);

        BookDto result = bookService.updateBookById(1L, newBookValues);

        Assertions.assertThat(updatedBookDto).isEqualTo(result);

    }

    @Test
    @DisplayName("Varify correctness of get all books from non empty DB")
    void findAll_nonEmptyBookTable_returnAllEntitiesFromDB() {
        Book book1 = new Book();
        book1.setId(1L)
                .setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of())
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        Book book2 = new Book();
        book2.setId(2L)
                .setTitle("Title2")
                .setAuthor("Author2")
                .setIsbn("123456790")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of())
                .setDescription("Description2")
                .setCoverImage("CoverImage2");

        Book book3 = new Book();
        book3.setId(3L)
                .setTitle("Title3")
                .setAuthor("Author3")
                .setIsbn("123456791")
                .setPrice(BigDecimal.valueOf(100))
                .setCategories(Set.of())
                .setDescription("Description3")
                .setCoverImage("CoverImage3");

        BookDto bookDto1 = new BookDto();
        bookDto1.setTitle("Title1")
                .setAuthor("Author1")
                .setIsbn("123456789")
                .setPrice(BigDecimal.valueOf(100))
                .setCategoriesIds(Set.of())
                .setDescription("Description1")
                .setCoverImage("CoverImage1");

        BookDto bookDto2 = new BookDto();
        bookDto2.setTitle("Title2")
                .setAuthor("Author2")
                .setIsbn("123456790")
                .setPrice(BigDecimal.valueOf(100))
                .setCategoriesIds(Set.of())
                .setDescription("Description2")
                .setCoverImage("CoverImage2");

        BookDto bookDto3 = new BookDto();
        bookDto3.setTitle("Title3")
                .setAuthor("Author3")
                .setIsbn("123456791")
                .setPrice(BigDecimal.valueOf(100))
                .setCategoriesIds(Set.of())
                .setDescription("Description3")
                .setCoverImage("CoverImage3");

        Page<Book> booksInDb = new PageImpl<Book>(List.of(book1, book2, book3));

        Mockito.when(bookRepository.findAll(Pageable.unpaged())).thenReturn(
                booksInDb);

        Mockito.when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        Mockito.when(bookMapper.toDto(book2)).thenReturn(bookDto2);
        Mockito.when(bookMapper.toDto(book3)).thenReturn(bookDto3);

        Page<BookDto> result = new PageImpl<>(List.of(bookDto1, bookDto2, bookDto3));
        /*Spy*/
        Page<BookDto> all = bookService.findAll(Pageable.unpaged());

        Assertions.assertThat(all.getTotalPages()).isEqualTo(result.getTotalPages());
        Assertions.assertThat(all.getTotalElements()).isEqualTo(result.getTotalElements());

        List<BookDto> resultList = result.stream().toList();
        List<BookDto> allList = all.stream().toList();

        for (int i = 0; i < resultList.size(); i++) {
            Assertions.assertThat(allList.get(i))
                    .isEqualTo(resultList.get(i));
            
        }

    }
}
