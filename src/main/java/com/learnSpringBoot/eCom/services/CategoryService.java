package com.learnSpringBoot.eCom.services;

import com.learnSpringBoot.eCom.model.Category;
import com.learnSpringBoot.eCom.payload.CategoryDTO;
import com.learnSpringBoot.eCom.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategoryById(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
