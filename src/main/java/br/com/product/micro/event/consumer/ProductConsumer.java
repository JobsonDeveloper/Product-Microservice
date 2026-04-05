package br.com.product.micro.event.consumer;

import br.com.product.micro.domain.*;
import br.com.product.micro.event.dto.DeliveryEventDto;
import br.com.product.micro.event.dto.ItemEventDto;
import br.com.product.micro.event.dto.SaleEventDto;
import br.com.product.micro.exception.*;
import br.com.product.micro.repository.IProductRepository;
import br.com.product.micro.repository.IReservedRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    private void saleCreated(SaleEventDto event) {
        String saleId = event.id();
        List<ItemEventDto> items = event.items();
        List<Product> productList = new ArrayList<>();

        items.forEach(product -> {
            Long barCode = product.barCode();
            Long subtrahend = product.quantity();

            Optional<Product> storageProduct = iProductRepository.findByBarCode(barCode);

            if (!storageProduct.isPresent()) {
                throw new ProductNotFoundException();
            }

            Product productUpdated = updateQuantity(storageProduct.get(), subtrahend);

            storageProduct.get().setQuantity(subtrahend);
            productList.add(storageProduct.get());
        });

        Reserved reserved = Reserved.builder()
                .saleId(saleId)
                .products(productList)
                .build();
        iReservedRepository.save(reserved);
    }

    private void saleCompleted(SaleEventDto eventDto) {
        String saleId = eventDto.id();
        Reserved reserved = iReservedRepository.findBySaleId(saleId).orElseThrow(ReservedProductsNotFoundException::new);
        iReservedRepository.deleteById(reserved.getId());
    }

    private void saleCanceled(SaleEventDto eventDto) {
        String saleId = eventDto.id();
        Reserved reserved = iReservedRepository.findBySaleId(saleId).orElseThrow(ProductNotFoundException::new);

        List<Product> products = reserved.getProducts();

        products.forEach(item -> {
            Optional<Product> storedProduct = iProductRepository.findById(item.getId());

            if (!storedProduct.isPresent()) {
                throw new ProductNotFoundException();
            }

            Long newProductQuantity = storedProduct.get().getQuantity() + item.getQuantity();
            Product product = Product.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .barCode(item.getBarCode())
                    .brand(item.getBrand())
                    .weight(item.getWeight())
                    .quantity(newProductQuantity)
                    .value(item.getValue())
                    .classification(item.getClassification())
                    .description(item.getDescription())
                    .manufacturingDate(item.getManufacturingDate())
                    .expirationDate(item.getExpirationDate())
                    .createdAt(item.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .build();

            iProductRepository.save(product);
        });

        iReservedRepository.deleteById(reserved.getId());
    }

    @KafkaListener(
            topics = "sale",
            groupId = "product-group",
            containerFactory = "saleKafkaListenerFactory"
    )
    public void saleListener(SaleEventDto event) {
        Status status = event.status();

        if (status.equals(Status.CREATED)) {
            saleCreated(event);
        }

        if (status.equals(Status.CANCELED)) {
            saleCanceled(event);
        }
    }

    @KafkaListener(
            topics = "delivery",
            groupId = "product-group",
            containerFactory = "deliveryKafkaListenerFactory"
    )
    public void deliveryListener(DeliveryEventDto event) {
        if(!event.status().equals(Status.DELIVERED)) return;

        Reserved reserved = iReservedRepository.findBySaleId(event.saleId()).orElseThrow(ReservedProductsNotFoundException::new);
        iReservedRepository.deleteById(reserved.getId());
    }

    private Product updateQuantity(Product product, Long subtrahend) {
        Long newProductQuantity = product.getQuantity() - subtrahend;

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
