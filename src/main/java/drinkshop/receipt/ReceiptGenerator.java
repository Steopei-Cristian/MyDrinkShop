package drinkshop.receipt;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReceiptGenerator {
    private ReceiptGenerator() {
        // private constructor to prevent instantiation
    }
    public static String generate(Order o, List<Product> products) {
        Map<Integer, Product> productsById = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        StringBuilder sb = new StringBuilder();
        sb.append("===== BON FISCAL =====\n").append("Comanda #").append(o.getId()).append("\n");
        for (OrderItem i : o.getItems()) {
            Product p = productsById.get(i.getProduct().getId());
            if (p == null) {
                throw new IllegalStateException("Product not found for item " + i.getProduct().getId());
            }
            sb.append(p.getNume()).append(": ").append(p.getPret()).append(" x ").append(i.getQuantity()).append(" = ").append(i.getTotal()).append(" RON\n");
        }
        sb.append("---------------------\nTOTAL: ").append(o.getTotalPrice()).append(" RON\n=====================\n");
        return sb.toString();
    }
}