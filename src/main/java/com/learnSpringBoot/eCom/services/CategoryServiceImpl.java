package com.learnSpringBoot.eCom.services;

import com.learnSpringBoot.eCom.exceptions.APIException;
import com.learnSpringBoot.eCom.exceptions.ResourceNotFoundException;
import com.learnSpringBoot.eCom.model.Category;
import com.learnSpringBoot.eCom.payload.CategoryDTO;
import com.learnSpringBoot.eCom.payload.CategoryResponse;
import com.learnSpringBoot.eCom.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

//    private List<Category> categoryList = new ArrayList<>();

    private CategoryRepository categoryRepository;

    private ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> allCategories = categoryPage.getContent();

        if (allCategories.isEmpty()) {
            throw new APIException("No Category Created till Now");
        }

        // Entity To DTO
        List<CategoryDTO> categoryDTOS = allCategories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();

        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // DTO to Entity
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryFromDB = categoryRepository.findByCategoryName(category.getCategoryName());
        if (categoryFromDB != null) {
            //System.out.println(savedCategory.getCategoryId()+" "+savedCategory.getCategoryName());
            throw new APIException("Category with " + category.getCategoryName() + " already Exists");
        }
        Category savedCategory = categoryRepository.save(category);

        // Converting Entity to DTO before Returning to controller
        CategoryDTO savedCategoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        return savedCategoryDTO;
    }

    @Override
    public CategoryDTO deleteCategoryById(Long categoryId) {
//        categoryList.remove(categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);
        categoryRepository.delete(category);

//        Category category = categoryList.stream().filter(x -> x.getCategoryId().equals(categoryId))
//                .findFirst()
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource Not Found"));
//        categoryList.remove(category);
//        categoryRepository.delete(category);
        return categoryDTO;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Optional<Category> savedCategoryOptional = categoryRepository.findById(categoryId);

        Category savedCategory = savedCategoryOptional.orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);

        CategoryDTO savedDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        return savedDTO;


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
