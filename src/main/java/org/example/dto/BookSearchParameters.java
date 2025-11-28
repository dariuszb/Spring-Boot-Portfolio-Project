package org.example.dto;

public record BookSearchParameters(String[] titles, String[] authors, String[] isbn) {
}
