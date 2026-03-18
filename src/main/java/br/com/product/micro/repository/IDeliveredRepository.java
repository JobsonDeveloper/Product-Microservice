package br.com.product.micro.repository;

import br.com.product.micro.domain.Delivered;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDeliveredRepository extends MongoRepository<Delivered, String> {
}
