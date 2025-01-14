package com.learnSpringBoot.eCom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
// From Server to Client
public class CategoryResponse {
    List<CategoryDTO> content;
}
