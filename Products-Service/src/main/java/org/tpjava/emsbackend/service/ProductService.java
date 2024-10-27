package org.tpjava.emsbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tpjava.emsbackend.model.Product;
import org.tpjava.emsbackend.repository.ProductsRepository;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;

    @Autowired
    public ProductService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

}
