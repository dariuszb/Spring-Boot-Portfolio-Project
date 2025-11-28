package org.example.dto;

public record BookSearchParametersDto(String[] titles, String[] authors, String[] isbn) {
}
