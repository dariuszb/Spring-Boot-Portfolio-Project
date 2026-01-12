package org.example.mappers;

import org.example.configuration.MapperConfiguration;
import org.example.dto.categorydto.CategoryDto;
import org.example.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfiguration.class)
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto categoryDto);

}
