package drinkshop.export;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvExporter {
    private CsvExporter() {
        throw new IllegalStateException("Utility class");
    }

    public static void exportOrders(List<Product> products, List<Order> orders, String path) {
        DecimalFormat priceFormat = new DecimalFormat("0.00");
        Map<Integer, Product> productsById = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        try (FileWriter w = new FileWriter(path)) {
            w.write("OrderId,Product,Quantity,Price\n");
            double sum = 0.0;
            for (Order o : orders) {
                for (OrderItem i : o.getItems()) {
                    Product p = productsById.get(i.getProduct().getId());
                    if (p == null) {
                        throw new IllegalStateException("Product not found for item " + i.getProduct().getId());
                    }
                    w.write(o.getId() + "," + escapeCsv(p.getNume()) + "," + i.getQuantity() + "," + priceFormat.format(i.getTotal()) + "\n");
                }
                w.write("total order: " + priceFormat.format(o.getTotal()) + " RON\n");
                w.write("-------------------------------\n");
                sum += o.getTotal();
            }
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            w.write("TOTAL OF " + date + " is: " + priceFormat.format(sum) + " RON\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }
}