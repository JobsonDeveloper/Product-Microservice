package br.com.product.micro.event.consumer;

import br.com.product.micro.domain.Item;
import br.com.product.micro.domain.Product;
import br.com.product.micro.domain.Reserved;
import br.com.product.micro.event.dto.ItemEventDto;
import br.com.product.micro.event.dto.SaleCreatedEventDto;
import br.com.product.micro.exception.ErrorUpdatingProductException;
import br.com.product.micro.exception.InsufficientProductsException;
import br.com.product.micro.exception.ProductNotFoundException;
import br.com.product.micro.repository.IProductRepository;
import br.com.product.micro.repository.IReservedRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductConsumer {
    private final IProductRepository iProductRepository;
    private final IReservedRepository iReservedRepository;

    public ProductConsumer(
            IProductRepository iProductRepository,
            IReservedRepository iReservedRepository
    ) {
        this.iProductRepository = iProductRepository;
        this.iReservedRepository = iReservedRepository;
    }

    @KafkaListener(topics = "sale-created", groupId = "product-group")
    public void consume(SaleCreatedEventDto event) {
        String saleId = event.id();
        List<ItemEventDto> items = event.items();
        List<Product> productList = new ArrayList<>();

        items.forEach(product -> {
            Long barCode = product.barCode();
            Long subtraendo = product.quantity();

            Optional<Product> storageProduct = iProductRepository.findByBarCode(barCode);

            if (!storageProduct.isPresent()) {
                throw new ProductNotFoundException();
            }

            Product productUpdated = updateQuantity(storageProduct.get(), subtraendo);

            storageProduct.get().setQuantity(subtraendo);
            productList.add(storageProduct.get());
        });

        Reserved reserved = Reserved.builder()
                .saleId(saleId)
                .products(productList)
                .build();
        iReservedRepository.save(reserved);
    }

    private Product updateQuantity(Product product, Long subtraendo) {
        Long newProductQuantity = product.getQuantity() - subtraendo;

        if (newProductQuantity < 0) {
            throw new InsufficientProductsException();
        }

        product.setQuantity(newProductQuantity);
        Product updated = iProductRepository.save(product);

        if (updated.getId() == null) {
            throw new ErrorUpdatingProductException();
        }

        return updated;
    }
}
