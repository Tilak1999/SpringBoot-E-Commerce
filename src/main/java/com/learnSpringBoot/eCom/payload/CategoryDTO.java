package com.learnSpringBoot.eCom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

// From client to server
public class CategoryDTO {
    private Long categoryId;
    private String categoryName;
}
