package org.example.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.dto.bookdto.BookDto;
import org.example.dto.bookdto.CreateBookRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(

            @Autowired DataSource dataSource) throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/delete-books-categories-book_categories.sql"));

        }
    }

    @Test
    @DisplayName("Verify that all books from DB are returned.")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = "classpath:database/insert-new-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-all-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_returnAllBooksInDB_success() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setTitle("First").setAuthor("FirstAuthor")
                .setIsbn("123456701").setPrice(BigDecimal
                        .valueOf(100.0)).setCategoriesIds(Set.of()));
        expected.add(new BookDto().setTitle("Second").setAuthor("SecondAuthor")
                .setIsbn("123456702").setPrice(BigDecimal
                        .valueOf(200.0)).setCategoriesIds(Set.of()));
        expected.add(new BookDto().setTitle("Third").setAuthor("ThirdAuthor")
                .setIsbn("123456703").setPrice(BigDecimal
                        .valueOf(300.0)).setCategoriesIds(Set.of()));

        MvcResult result = mockMvc.perform(
                        get("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String httpResponse = result.getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(httpResponse);

        JsonNode content = node.get("content");

        List<BookDto> actual = objectMapper.convertValue(
                content, new TypeReference<List<BookDto>>() {
                });

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat(actual.get(i)).usingRecursiveComparison()
                    .ignoringFields("id", "price")
                    .isEqualTo(expected.get(i));
        }
    }

    @Test
    @DisplayName("Verify that book with chosen id is returned")
    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(scripts = "classpath:database/insert-new-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-all-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBookById_correctInputParams_returnBookDto() throws Exception {
        BookDto expected = new BookDto()
                .setId(1L)
                .setTitle("First")
                .setAuthor("FirstAuthor")
                .setIsbn("123456701")
                .setPrice(BigDecimal.valueOf(100.0))
                .setCategoriesIds(Set.of());

        MvcResult result = mockMvc.perform(
                        get("/api/books/{id}", expected.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String httpResponse = result.getResponse().getContentAsString();

        BookDto bookDto = objectMapper.readValue(httpResponse, new TypeReference<BookDto>() {
        });
        EqualsBuilder.reflectionEquals(expected, bookDto, "price");
    }

    @Test
    @DisplayName("Varify that new book is created by method")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-all-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_correctInputParams_ok() throws Exception {

        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto(
                "Title11", "Author11", "1234111111", BigDecimal.valueOf(200),
                Set.of(), "Description11", "CoveImage11");

        BookDto expected = new BookDto()
                .setTitle(createBookRequestDto.title())
                .setAuthor(createBookRequestDto.author())
                .setIsbn(createBookRequestDto.isbn())
                .setPrice(createBookRequestDto.price())
                .setCategoriesIds(createBookRequestDto.categoriesIds())
                .setDescription(createBookRequestDto.description())
                .setCoverImage(createBookRequestDto.coverImage());

        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);

        MvcResult result = mockMvc.perform(
                        post("/api/books")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), BookDto.class);

        EqualsBuilder.reflectionEquals(expected, actual);

    }

    @Test
    @DisplayName("Verify correctness of update book with chosen id")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/insert-new-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_correctInputParams_ok() throws Exception {

        Long idOfBookToUpdate = 2L;

        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto(
                "Title11", "Author11", "1234111111", BigDecimal.valueOf(200),
                Set.of(), "Description11", "CoveImage11");

        BookDto expected = new BookDto()
                .setId(idOfBookToUpdate)
                .setTitle(createBookRequestDto.title())
                .setAuthor(createBookRequestDto.author())
                .setIsbn(createBookRequestDto.isbn())
                .setPrice(createBookRequestDto.price())
                .setCategoriesIds(createBookRequestDto.categoriesIds())
                .setDescription(createBookRequestDto.description())
                .setCoverImage(createBookRequestDto.coverImage());

        String jsonRequest = objectMapper.writeValueAsString(createBookRequestDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/books/{id}", idOfBookToUpdate)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String httpResponse = result.getResponse().getContentAsString();

        BookDto actual = objectMapper.readValue(httpResponse, new TypeReference<BookDto>() {
        });

        EqualsBuilder.reflectionEquals(expected, actual);

    }
}
