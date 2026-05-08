package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step 4 – Full integration test: real ProductService (S) + real ProductValidator (V)
 * + real Product (E) + in-memory Repository (R).
 * No mocks at all; validates end-to-end behaviour without file I/O.
 */
class ProductServiceIntegrationFullTest {

    private ProductService service;

    @BeforeEach
    void setUp() {
        // Lightweight in-memory repository – no file I/O
        AbstractRepository<Integer, Product> inMemoryRepo = new AbstractRepository<>() {
            @Override
            protected Integer getId(Product entity) {
                return entity.getId();
            }
        };
        service = new ProductService(inMemoryRepo, new ProductValidator());
    }

    /**
     * Adding a valid product → findById returns the same product.
     */
    @Test
    void addProduct_validProduct_canBeFoundById() {
        Product product = new Product(1, "Espresso", 8.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        service.addProduct(product);

        Product found = service.findById(1);
        assertEquals(product, found);
    }

    /**
     * Adding an invalid product throws and leaves the repository empty.
     */
    @Test
    void addProduct_invalidProduct_notStored() {
        Product product = new Product(0, "", -1.0,
                CategorieBautura.TEA, TipBautura.WATER_BASED);

        assertThrows(ValidationException.class, () -> service.addProduct(product));

        List<Product> all = service.getAllProducts();
        assertTrue(all.isEmpty());
    }

    /**
     * Two valid products are stored and both appear in getAllProducts().
     */
    @Test
    void addTwoProducts_bothReturnedByGetAll() {
        Product p1 = new Product(1, "Espresso", 8.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        Product p2 = new Product(2, "Green Tea", 4.5,
                CategorieBautura.TEA, TipBautura.WATER_BASED);

        service.addProduct(p1);
        service.addProduct(p2);

        List<Product> all = service.getAllProducts();
        assertEquals(2, all.size());
        assertTrue(all.contains(p1));
        assertTrue(all.contains(p2));
    }

    /**
     * deleteProduct removes the product from the repository.
     */
    @Test
    void deleteProduct_removesItFromRepo() {
        Product product = new Product(3, "Latte", 6.0,
                CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        service.addProduct(product);
        assertNotNull(service.findById(3));

        service.deleteProduct(3);
        assertNull(service.findById(3));
    }
}
