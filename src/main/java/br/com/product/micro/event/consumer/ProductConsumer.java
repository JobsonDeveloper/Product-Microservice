package br.com.product.micro.event.consumer;

import br.com.product.micro.domain.Product;
import br.com.product.micro.domain.Purchased;
import br.com.product.micro.domain.Reserved;
import br.com.product.micro.domain.Status;
import br.com.product.micro.event.dto.ItemEventDto;
import br.com.product.micro.event.dto.PaymentEventDto;
import br.com.product.micro.event.dto.SaleEventDto;
import br.com.product.micro.exception.ErrorUpdatingProductException;
import br.com.product.micro.exception.InsufficientProductsException;
import br.com.product.micro.exception.ProductNotFoundException;
import br.com.product.micro.exception.ReservedProductsNotFoundException;
import br.com.product.micro.repository.IProductRepository;
import br.com.product.micro.repository.IPurchasedRepository;
import br.com.product.micro.repository.IReservedRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

@Service
public class ProductConsumer {
    private final IProductRepository iProductRepository;
    private final IReservedRepository iReservedRepository;
    private final IPurchasedRepository iPurchasedRepository;

    public ProductConsumer(
            IProductRepository iProductRepository,
            IReservedRepository iReservedRepository, IPurchasedRepository iPurchasedRepository
    ) {
        this.iProductRepository = iProductRepository;
        this.iReservedRepository = iReservedRepository;
        this.iPurchasedRepository = iPurchasedRepository;
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

    @KafkaListener(
            topics = "sale",
            groupId = "product-group",
            containerFactory = "saleKafkaListenerFactory"
    )
    public void updateStatus(SaleEventDto event) {
        Status status = event.status();
        Status created = Status.CREATED;

        if (status.equals(created)) {
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
    }

    @KafkaListener(
            topics = "payment",
            groupId = "product-group",
            containerFactory = "paymentKafkaListenerFactory"
    )
    public void productPurchased(PaymentEventDto paymentEventDto) {
        Status status = paymentEventDto.status();

        if (status.equals(Status.PAID)) {
            String saleId = paymentEventDto.saleId();

            Optional<Reserved> reservedProducts = iReservedRepository.findBySaleId(saleId);

            if (!reservedProducts.isPresent()) {
                throw new ReservedProductsNotFoundException();
            }

            Purchased purchasedProduct = Purchased.builder()
                    .saleId(saleId)
                    .products(reservedProducts.get().getProducts())
                    .build();

            Purchased newPurchasedProduct = iPurchasedRepository.save(purchasedProduct);

            if (newPurchasedProduct.getId() != null) {
                iReservedRepository.delete(reservedProducts.get());
            }
        }
    }
}
