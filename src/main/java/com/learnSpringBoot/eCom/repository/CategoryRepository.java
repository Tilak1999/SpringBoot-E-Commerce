package com.learnSpringBoot.eCom.repository;

import com.learnSpringBoot.eCom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    Category findByCategoryName(String categoryName);

}
