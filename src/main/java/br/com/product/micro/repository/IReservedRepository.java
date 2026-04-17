package br.com.product.micro.repository;

import br.com.product.micro.domain.Reserved;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface IReservedRepository extends MongoRepository<Reserved, String> {
    public Optional<Reserved> findBySaleId(String saleId);
    public Optional<Reserved> deleteBySaleId(String saleId);
}
