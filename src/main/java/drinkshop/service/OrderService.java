package drinkshop.service;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.validator.OrderValidator;

import java.util.List;

public class OrderService {

    private final Repository<Integer, Order> orderRepo;
    private final Repository<Integer, Product> productRepo;
    private final OrderValidator validator;

    public OrderService(Repository<Integer, Order> orderRepo, Repository<Integer, Product> productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.validator = new OrderValidator();

    }

    public void addOrder(Order o) {
        validator.validate(o);
        orderRepo.save(o);
    }

    public void updateOrder(Order o) {
        validator.validate(o);
        orderRepo.update(o);
    }

    public void deleteOrder(int id) {
        orderRepo.delete(id);
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Order findById(int id) {
        return orderRepo.findOne(id);
    }

    public double computeTotal(Order o) {
        return o.getItems().stream()
                .mapToDouble(item -> {
                    Product product = productRepo.findOne(item.getProduct().getId());
                    if (product == null) {
                        throw new IllegalStateException("Product not found for item " + item.getProduct().getId());
                    }
                    return product.getPret() * item.getQuantity();
                })
                .sum();
    }

    public void addItem(Order o, OrderItem item) {
        o.addItem(item);
        validator.validate(o);
        orderRepo.update(o);
    }

    public void removeItem(Order o, OrderItem item) {
        o.removeItem(item);
        validator.validate(o);
        orderRepo.update(o);
    }
}