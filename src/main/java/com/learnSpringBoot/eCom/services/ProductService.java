package com.learnSpringBoot.eCom.services;

import com.learnSpringBoot.eCom.model.Product;
import com.learnSpringBoot.eCom.payload.ProductDTO;
import com.learnSpringBoot.eCom.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(Long categoryId,ProductDTO productDTO);

    ProductResponse getAllProducts();

    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchProductsByKeyword(String keyword);

    ProductDTO updateProduct(Long productId, ProductDTO productDTO);

    ProductDTO deleteProduct(Long productId);
}
