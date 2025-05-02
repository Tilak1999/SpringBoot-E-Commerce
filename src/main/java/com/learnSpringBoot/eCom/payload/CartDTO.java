package com.learnSpringBoot.eCom.payload;


import com.learnSpringBoot.eCom.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Long cartId;
    private Double totalPrice;
    private List<ProductDTO> products;
}
