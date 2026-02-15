package br.com.product.micro.repository;

import br.com.product.micro.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProductRepository extends MongoRepository<Product, String> {
    public Optional<Product> findByBarCode(Long code);
}
