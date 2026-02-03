package org.example.springbootportfolioproject.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.dto.bookdto.BookDto;
import org.example.dto.bookdto.CreateBookRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll(

            @Autowired WebApplicationContext applicationContext) throws SQLException {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void afterEach(
            @Autowired DataSource datasource
    ) {
        teardown(datasource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-all-books.sql")
            );
        }

    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @Sql(scripts = "classpath:database/insert-new-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Varify that all books from DB are returned.")
    void findAll_returnAllBooksInDB_success() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setTitle("First").setAuthor("FirstAuthor")
                .setIsbn("123456701").setPrice(BigDecimal.valueOf(100)).setCategoriesIds(Set.of()));
        expected.add(new BookDto().setTitle("Second").setAuthor("SecondAuthor")
                .setIsbn("123456702").setPrice(BigDecimal.valueOf(200)).setCategoriesIds(Set.of()));
        expected.add(new BookDto().setTitle("Third").setAuthor("ThirdAuthor")
                .setIsbn("123456703").setPrice(BigDecimal.valueOf(300)).setCategoriesIds(Set.of()));

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

        Assertions.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat(actual.get(i)).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expected.get(i));
        }
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @Sql(scripts = "classpath:database/insert-new-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Varify that book with chosen id is returned")
    void getBookById_correctInputParams_returnBookDto() throws Exception {
        BookDto expected = new BookDto()
                .setId(1L)
                .setTitle("First")
                .setAuthor("FirstAuthor")
                .setIsbn("123456701")
                .setPrice(BigDecimal.valueOf(100))
                .setCategoriesIds(Set.of());

        MvcResult result = mockMvc.perform(
                        get("/api/books/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String httpResponse = result.getResponse().getContentAsString();

        BookDto bookDto = objectMapper.readValue(httpResponse, new TypeReference<BookDto>() {
        });

        Assertions.assertEquals(expected, bookDto);

    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/delete-all-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Varify that new book is created by method")
    void createBook_correctInputParams_ok() throws Exception {

        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto(
                "Title11", "Author11", "1234111111", BigDecimal.valueOf(200),
                Set.of(1L), "Description11", "CoveImage11");

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

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/insert-new-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Varify correctness of update book with chosen id")
    void updateBook_correctInputParams_ok() throws Exception {

        Long idOfBookToUpdate = 2L;

        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto(
                "Title11", "Author11", "1234111111", BigDecimal.valueOf(200),
                Set.of(1L), "Description11", "CoveImage11");

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
