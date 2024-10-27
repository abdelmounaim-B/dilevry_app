package org.tpjava.emsbackend.controller;

import org.tpjava.emsbackend.exception.ResourceNotFoundException;
import org.tpjava.emsbackend.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tpjava.emsbackend.repository.ProductsRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("products")
public class ProductController {

    @Autowired
    private ProductsRepository productsRepository;
    // Get all employees
    @GetMapping("/getAll")
    public List<Product> getAllProducts() {
        return productsRepository.findAll();
    }

    // Create a new employee
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productsRepository.save(product);
        return ResponseEntity.ok(savedProduct); // You can return a 201 status here as well if preferred
    }

    // Get employee by id
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not exist with id: " + id));
        return ResponseEntity.ok(product);
    }

    // Update employee
    @PutMapping
    public ResponseEntity<Product> updateProduct(@RequestBody Product productDetails) {
        Product product = productsRepository.findById(productDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not exist with id: " + productDetails.getId()));
        // Update product details
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setImageLink(productDetails.getImageLink());
        product.setQuantity(productDetails.getQuantity());
        product.setCategory(productDetails.getCategory());
        product.setSku(productDetails.getSku());

        Product updatedProduct = productsRepository.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{id}")
    public Boolean updateProductQuantity(@PathVariable Long id, @RequestBody Integer quantity) {
        Product product = productsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not exist with id: " + id));
        // Update product details
        product.setQuantity(product.getQuantity() - quantity);

        Product updatedProduct = productsRepository.save(product);
        return true;
    }


    // Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteProduct(@PathVariable Long id) {
//        Product employee = employeeRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not exist with id: " + id));
        if (productsRepository.existsById(id)){
            productsRepository.deleteById(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        }else {
            return (ResponseEntity<Map<String, Boolean>>) ResponseEntity.notFound();
        }

    }
}
