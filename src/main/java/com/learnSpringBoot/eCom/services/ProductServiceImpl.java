package com.learnSpringBoot.eCom.services;

import com.learnSpringBoot.eCom.exceptions.APIException;
import com.learnSpringBoot.eCom.exceptions.ResourceNotFoundException;
import com.learnSpringBoot.eCom.model.Category;
import com.learnSpringBoot.eCom.model.Product;
import com.learnSpringBoot.eCom.payload.ProductDTO;
import com.learnSpringBoot.eCom.payload.ProductResponse;
import com.learnSpringBoot.eCom.repository.CategoryRepository;
import com.learnSpringBoot.eCom.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("category", "categoryId", categoryId));

        // Check if the product already exists
        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if (isProductNotPresent) {
            // DTO to Entity
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("Default.png");
            product.setCategory(category);

            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            productDTO.setSpecialPrice(specialPrice);

            Product savedProduct = productRepository.save(product);

            return modelMapper.map(savedProduct, ProductDTO.class);
        } else {
            throw new APIException("Product already exists");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);


        List<Product> allProducts = pageProducts.getContent();
        List<ProductDTO> allProductsDTOS = allProducts.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        // check if products is Empty
        if (allProducts.isEmpty()) {
            throw new APIException("No Products Exists");
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(allProductsDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> products = pageProducts.getContent();

        // Finding the products for a particular category
        // List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        // Entity to DTO
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
        if (products.isEmpty()) {
            throw new APIException(category.getCategoryName() + " Category does not have any products");
        }

        ProductResponse productResponse = new ProductResponse();
        // adding List of ProductDTOS to Product Response
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> products = pageProducts.getContent();

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        if (products.isEmpty()) {
            throw new APIException("Products Not Found with Keyword:" + keyword);
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        // Get the product from the database
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // DTO to Entity
        Product product = modelMapper.map(productDTO, Product.class);

        // Updating the product
        productFromDB.setProductName(product.getProductName());
        productFromDB.setDescription(product.getDescription());
        productFromDB.setQuantity(product.getQuantity());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setDiscount(product.getDiscount());

        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        productFromDB.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepository.save(productFromDB);
        ProductDTO savedProductDTO = modelMapper.map(savedProduct, ProductDTO.class);
        return savedProductDTO;
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "productId", productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Get the product from the database
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Upload the image to the server
        // Get the file name of the uploaded image
        String path = "images/";
        String fileName = fileService.uploadImage(path, image);

        // Updating the new file name to the product
        productFromDB.setImage(fileName);

        // save the updated product
        Product updatedProduct = productRepository.save(productFromDB);

        // return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }


}
