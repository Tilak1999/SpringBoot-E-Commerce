package com.learnSpringBoot.eCom.services;

import com.learnSpringBoot.eCom.exceptions.APIException;
import com.learnSpringBoot.eCom.exceptions.ResourceNotFoundException;
import com.learnSpringBoot.eCom.model.Category;
import com.learnSpringBoot.eCom.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

//    private List<Category> categoryList = new ArrayList<>();

    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> allCategories = categoryRepository.findAll();
        if (allCategories.isEmpty()) {
            throw new APIException("No Category Created till Now");
        }
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null) {
            //System.out.println(savedCategory.getCategoryId()+" "+savedCategory.getCategoryName());
            throw new APIException("Category with " + category.getCategoryName() + " already Exists");
        }
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategoryById(Long categoryId) {
//        categoryList.remove(categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        categoryRepository.delete(category);

//        Category category = categoryList.stream().filter(x -> x.getCategoryId().equals(categoryId))
//                .findFirst()
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found"));
//        categoryList.remove(category);
//        categoryRepository.delete(category);
        return "Category with id: " + categoryId + " deleted Successfully !";
    }

    @Override
    public Category updateCategory(Category category, Long categoryId) {
        Optional<Category> savedCategoryOptional = categoryRepository.findById(categoryId);

        Category savedCategory = savedCategoryOptional.orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return savedCategory;


//        Optional<Category> optionalCategory = categoryList.stream()
//                .filter(x -> x.getCategoryId().equals(categoryId))
//                .findFirst();
//
//        if (optionalCategory.isPresent()) {
//            Category existingCategory = optionalCategory.get();
//            existingCategory.setCategoryName(category.getCategoryName());
//            Category updatedCategory = categoryRepository.save(existingCategory);
//            return updatedCategory;
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found");
//        }
    }
}
