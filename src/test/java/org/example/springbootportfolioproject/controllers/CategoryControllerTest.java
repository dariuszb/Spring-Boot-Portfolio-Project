package org.example.springbootportfolioproject.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.example.dto.categorydto.CategoryDto;
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
class CategoryControllerTest {

    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/delete-books-categories-book_categories.sql"
                    ));
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/delete-added-category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Varify correctness of creation"
            + " new instance of category entity")
    void createCategory_createInputParams_ok() throws Exception {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Category 1");
        categoryDto.setDescription("Category 1");

        String jsonRequest = objectMapper.writeValueAsString(categoryDto);

        MvcResult result = mockMvc.perform(
                        post("/api/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), CategoryDto.class);

        EqualsBuilder.reflectionEquals(categoryDto, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Check, that method return all inputs from DB")
    @Sql(scripts = "classpath:database/insert-categories-to-categories-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void getAll_returnAllCategoriesInDB() throws Exception {

        List<CategoryDto> categoryDtoList = getCategoryDtos();

        MvcResult result = mockMvc.perform(
                        get("/api/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String httpResponse = result.getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(httpResponse);

        JsonNode content = node.get("content");

        List<CategoryDto> actual = objectMapper.convertValue(
                content, new TypeReference<List<CategoryDto>>() {
                });

        List<CategoryDto> sorted = actual.stream()
                .sorted(Comparator.comparing(CategoryDto::getId)).toList();
        Assertions.assertEquals(categoryDtoList.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat(categoryDtoList.get(i)).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(sorted.get(i));
        }

    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Varify the correctness of getting with chosen id")
    void getCategoryById() throws Exception {

        Long chosenCategoryId = 2L;

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setName("crime story");
        categoryDto2.setDescription("crime story");

        MvcResult result = mockMvc.perform(
                        get("/api/categories/{id}", chosenCategoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String httpResponse = result.getResponse().getContentAsString();

        CategoryDto categoryDtoResult = objectMapper.readValue(httpResponse,
                new TypeReference<CategoryDto>() {
            });

        EqualsBuilder.reflectionEquals(categoryDtoResult, categoryDto2);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(scripts = "classpath:database/back-to-values-before-update.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Varify correctness of updating category with chosen id")
    void updateCategory_correctInputParams_ok() throws Exception {

        Long categoryIdToUpdate = 2L;

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Updated category name");
        categoryDto.setDescription("Updated category description");

        String jsonRequest = objectMapper.writeValueAsString(categoryDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/categories/{id}", categoryIdToUpdate)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String httpResponse = result.getResponse().getContentAsString();

        CategoryDto actual = objectMapper.readValue(httpResponse,
            new TypeReference<CategoryDto>() {
            });

        EqualsBuilder.reflectionEquals(categoryDto, actual);

    }

    private static List<CategoryDto> getCategoryDtos() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("novel");
        categoryDto.setDescription("novel");

        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setId(2L);
        categoryDto2.setName("crime story");
        categoryDto2.setDescription("crime story");

        CategoryDto categoryDto3 = new CategoryDto();
        categoryDto3.setId(3L);
        categoryDto3.setName("horror");
        categoryDto3.setDescription("horror");

        List<CategoryDto> categoryDtoList = new ArrayList<>();
        categoryDtoList.add(categoryDto);
        categoryDtoList.add(categoryDto2);
        categoryDtoList.add(categoryDto3);
        return categoryDtoList;
    }
}
