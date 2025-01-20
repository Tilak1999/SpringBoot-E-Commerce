package com.learnSpringBoot.eCom.controller;

import com.learnSpringBoot.eCom.config.AppConstants;
import com.learnSpringBoot.eCom.payload.CategoryDTO;
import com.learnSpringBoot.eCom.payload.CategoryResponse;
import com.learnSpringBoot.eCom.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    // @GetMapping("/public/categories")
    @RequestMapping(value = "/public/categories", method = RequestMethod.GET)
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategoryById(@PathVariable Long categoryId) {

        CategoryDTO deletedCategory = categoryService.deleteCategoryById(categoryId);
        return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
//            return ResponseEntity.ok(status);
//            return ResponseEntity.status(HttpStatus.OK).body(status);
    }

    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId) {

        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(updatedCategoryDTO, HttpStatus.OK);

    }
}
