package br.com.product.micro.service;

import br.com.product.micro.controller.dto.ReturnAllProductsDto;
import br.com.product.micro.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {
    public Product createProduct(Product product);
    public void deleteProduct(Long code);
    public Product getProduct(Long code);
    public Page<ReturnAllProductsDto> listProduct(Pageable pageable);
    public Product updateProduct(Product product);
}
