package br.com.product.micro.service;

import br.com.product.micro.controller.dto.ReturnAllProductsDto;
import br.com.product.micro.domain.Product;
import br.com.product.micro.exception.ErrorCreatingProductException;
import br.com.product.micro.exception.ProductAlreadyRegisteredException;
import br.com.product.micro.exception.ProductNotFoundException;
import br.com.product.micro.repository.IProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductService implements IProductService {
    private final IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        Optional<Product> existingProduct = productRepository.findByBarCode(product.getBarCode());

        if (existingProduct.isPresent()) {
            throw new ProductAlreadyRegisteredException();
        }

        Product newProduct = productRepository.save(product);

        if(newProduct.getId().isBlank()) {
            throw new ErrorCreatingProductException();
        }

        return newProduct;
    }

    @Override
    public void deleteProduct(Long code) {

    }

    @Override
    public Product getProduct(Long code) {
        return null;
    }

    @Override
    public Page<ReturnAllProductsDto> listProduct(Pageable pageable) {
        return null;
    }

    @Override
    public Product updateProduct(Product product) {
        Optional<Product> existingProduct = productRepository.findByBarCode(product.getBarCode());

        if(!existingProduct.isPresent()) {
            throw new ProductNotFoundException();
        }

        product.setId(existingProduct.get().getId());
        product.setCreatedAt(existingProduct.get().getCreatedAt());

        Product updatedProduct = productRepository.save(product);

        return updatedProduct;
    }
}
