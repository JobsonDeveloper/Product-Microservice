package br.com.product.micro.service;

import br.com.product.micro.dto.request.PurchaseProductDto;
import br.com.product.micro.domain.Product;
import br.com.product.micro.exception.*;
import br.com.product.micro.repository.IProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService implements IProductService {
    private final IProductRepository productRepository;

    public ProductService(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        boolean existingProduct = productRepository.existsByBarCode(product.getBarCode());
        if (existingProduct) throw new ProductAlreadyRegisteredException();

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long code) {
        Product product = productRepository.findByBarCode(code).orElseThrow(ProductNotFoundException::new);
        productRepository.deleteById(product.getId());
    }

    @Override
    public Product getProduct(Long code) {
        return productRepository.findByBarCode(code).orElseThrow(ProductNotFoundException::new);
    }

    @Override
    public Page<Product> listProduct(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> new Product(
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
        if(product.getQuantity() < 0) throw new InsufficientProductsException("Don't is possible set this product quantity!");

        Product existingProduct = productRepository.findByBarCode(product.getBarCode()).orElseThrow(ProductNotFoundException::new);
        product.setId(existingProduct.getId());
        product.setCreatedAt(existingProduct.getCreatedAt());

        return productRepository.save(product);
    }

    @Override
    public Product removeProductQuantity(PurchaseProductDto productDto) {
        Long barCode = productDto.barCode();
        Long quantityPurchased = productDto.quantityPurchased();
        Product product = getProduct(barCode);
        Long quantity = product.getQuantity();

        if((quantity - quantityPurchased) < 0) throw new InsufficientProductsException();

        product.setQuantity(quantity - quantityPurchased);

        return updateProduct(product);
    }
}
