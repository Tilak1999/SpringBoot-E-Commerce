package com.learnSpringBoot.eCom.services;

import com.learnSpringBoot.eCom.model.Category;
import com.learnSpringBoot.eCom.payload.CategoryDTO;
import com.learnSpringBoot.eCom.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories();

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    String deleteCategoryById(Long categoryId);

    Category updateCategory(Category category, Long categoryId);
}
