package org.example.dto.bookdto;

public record BookSearchParametersDto(String[] titles, String[] authors, String[] isbn) {
}
