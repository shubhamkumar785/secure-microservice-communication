package com.example.product_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.product_service.entity.Product;
import com.example.product_service.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger log =
            LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {

        log.info("Creating product");

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully. ProductId: {}",
                savedProduct.getId());

        return savedProduct;
    }

    public Product getProductById(Long id) {

        log.info("Fetching product with id: {}", id);

        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);

                    return new RuntimeException(
                            "Product not found with id: " + id);
                });
    }

    public List<Product> getAllProducts() {

        log.info("Fetching all products");

        List<Product> products = productRepository.findAll();

        log.info("Total products found: {}", products.size());

        return products;
    }
}