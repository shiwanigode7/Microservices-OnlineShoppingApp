package com.programmingtechie.Product_Service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.programmingtechie.Product_Service.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {

}
