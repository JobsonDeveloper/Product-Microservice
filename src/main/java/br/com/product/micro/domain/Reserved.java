package br.com.product.micro.domain;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@Document(collection = "reserved")
public class Reserved {
    @Id
    private String id;
    private String saleId;
    private List<Product> products;
}
