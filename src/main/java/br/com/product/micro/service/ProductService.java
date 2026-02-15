package br.com.product.micro.service;

import br.com.product.micro.controller.dto.PurchaseProductDto;
import br.com.product.micro.controller.dto.ReturnAllProductsDto;
import br.com.product.micro.domain.Product;
import br.com.product.micro.exception.*;
import br.com.product.micro.repository.IProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

        if (newProduct.getId().isBlank()) {
            throw new ErrorCreatingProductException();
        }

        return newProduct;
    }

    @Override
    public Boolean deleteProduct(Long code) {
        Optional<Product> product = productRepository.findByBarCode(code);

        if (!product.isPresent()) {
            throw new ProductNotFoundException();
        }

        productRepository.deleteById(product.get().getId());
        Optional<Product> deletedProduct = productRepository.findByBarCode(code);

        if(deletedProduct.isPresent()) {
            throw new ErrorDeletingProductException();
        }

        return true;
    }

    @Override
    public Product getProduct(Long code) {
        Optional<Product> product = productRepository.findByBarCode(code);

        if(!product.isPresent()) {
            throw new ProductNotFoundException();
        }

        return product.get();
    }

    @Override
    public Page<ReturnAllProductsDto> listProduct(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> new ReturnAllProductsDto(
                        product.getId(),
                        product.getName(),
                        product.getBarCode(),
                        product.getBrand(),
                        product.getWeight(),
                        product.getQuantity(),
                        product.getValue(),
                        product.getClassification(),
                        product.getDescription(),
                        product.getManufacturingDate(),
                        product.getExpirationDate(),
                        product.getCreatedAt(),
                        product.getUpdatedAt()
                ));
    }

    @Override
    public Product updateProduct(Product product) {
        Optional<Product> existingProduct = productRepository.findByBarCode(product.getBarCode());

        if (!existingProduct.isPresent()) {
            throw new ProductNotFoundException();
        }

        product.setId(existingProduct.get().getId());
        product.setCreatedAt(existingProduct.get().getCreatedAt());

        Product updatedProduct = productRepository.save(product);

        return updatedProduct;
    }

    @Override
    public Product removeProductQuantity(PurchaseProductDto productDto) {
        Long barCode = productDto.barCode();
        Long quantityPurchased = productDto.quantityPurchased();

        Product product = getProduct(barCode);
        Long quantity = product.getQuantity();

        if((quantity - quantityPurchased) < 0) {
            throw new InsufficientProductsException();
        }

        product.setQuantity(quantity - quantityPurchased);

        return updateProduct(product);
    }
}
