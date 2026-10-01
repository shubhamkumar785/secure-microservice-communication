package com.example.order_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.order_service.dto.ProductResponse;
import com.example.order_service.entity.Order;
import com.example.order_service.repository.OrderRepository;

@Service
public class OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final RestClient restClient;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public OrderService(
            OrderRepository orderRepository,
            RestClient restClient) {

        this.orderRepository = orderRepository;
        this.restClient = restClient;
    }

    public Order createOrder(Order order) {

        log.info("Creating order for productId: {}, quantity: {}",
                order.getProductId(), order.getQuantity());

        // Call Product Service
        log.info("Calling Product Service for productId: {}",
                order.getProductId());

        ProductResponse product = restClient.get()
                .uri(productServiceUrl + "/products/" + order.getProductId())
                .retrieve()
                .body(ProductResponse.class);

        // Check product response
        if (product == null) {
            log.error("Product Service returned no data for productId: {}",
                    order.getProductId());

            throw new RuntimeException("Product service returned no data");
        }

        log.info("Product received. ProductId: {}, Available stock: {}",
                order.getProductId(), product.getStock());

        // Check product stock
        if (product.getStock() < order.getQuantity()) {

            log.warn(
                "Insufficient stock for productId: {}. Requested: {}, Available: {}",
                order.getProductId(),
                order.getQuantity(),
                product.getStock()
            );

            throw new RuntimeException(
                    "Insufficient stock. Available stock: "
                            + product.getStock());
        }

        // Save order
        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully. OrderId: {}",
                savedOrder.getId());

        return savedOrder;
    }

    public List<Order> getAllOrders() {

        log.info("Fetching all orders");

        List<Order> orders = orderRepository.findAll();

        log.info("Total orders found: {}", orders.size());

        return orders;
    }

    public Order getOrderById(Long id) {

        log.info("Fetching order with id: {}", id);

        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found with id: {}", id);

                    return new RuntimeException(
                            "Order not found with id: " + id);
                });
    }
}