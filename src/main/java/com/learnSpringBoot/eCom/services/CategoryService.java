package com.learnSpringBoot.eCom.services;

import com.learnSpringBoot.eCom.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    void createCategory(Category category);

    String deleteCategoryById(Long categoryId);

    Category updateCategory(Category category, Long categoryId);
}
