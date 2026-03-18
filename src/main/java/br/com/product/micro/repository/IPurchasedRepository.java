package br.com.product.micro.repository;

import br.com.product.micro.domain.Purchased;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPurchasedRepository extends MongoRepository<Purchased, String> {
    public Optional<Purchased> findBySaleId(String saleId);
}
