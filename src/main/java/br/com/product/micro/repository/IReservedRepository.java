package br.com.product.micro.repository;

import br.com.product.micro.domain.Reserved;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IReservedRepository extends MongoRepository<Reserved, String> {
}
