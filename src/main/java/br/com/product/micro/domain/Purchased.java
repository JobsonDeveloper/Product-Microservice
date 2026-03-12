package br.com.product.micro.domain;

import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@SuperBuilder
@Document(collection = "purchased")
public class Purchased extends Reserved {
}
