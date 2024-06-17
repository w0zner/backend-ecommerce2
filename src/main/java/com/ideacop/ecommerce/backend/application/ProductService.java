package com.ideacop.ecommerce.backend.application;

import com.ideacop.ecommerce.backend.domain.model.Product;
import com.ideacop.ecommerce.backend.domain.port.IProductRepository;

public class ProductService {
    private final IProductRepository iProductRepository;


    public ProductService(IProductRepository iProductRepository) {
        this.iProductRepository = iProductRepository;
    }

    public Product save(Product product) {
        return iProductRepository.save(product);
    }

    public Iterable<Product> findAll() {
        return iProductRepository.findAll();
    }

    public Product findById(Integer id) {
        return iProductRepository.findById(id);
    }

    public void deleteById(Integer id) {
        iProductRepository.deleteById(id);
    }
}
