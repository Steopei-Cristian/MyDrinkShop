package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Step 2 – Integration test: real ProductValidator (V) + mock Repository (R).
 * Product (E) is a real POJO with controlled field values — mocking a simple
 * value object is unnecessary and problematic on newer JVMs.
 * Validates that the real validator correctly guards the real service method.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceIntegrationVTest {

    @Mock
    private Repository<Integer, Product> mockRepo;

    private ProductService service;

    @BeforeEach
    void setUp() {
        // Real V wired in; R stays mocked
        service = new ProductService(mockRepo, new ProductValidator());
    }

    /**
     * Real product with valid data → real validator passes → save is called.
     * Demonstrates assert (no exception) and verify (save interaction).
     */
    @Test
    void addProduct_validProductData_saveCalled() {
        Product product = new Product(1, "Cola", 5.0,
                CategorieBautura.JUICE, TipBautura.WATER_BASED);

        assertDoesNotThrow(() -> service.addProduct(product));

        verify(mockRepo, times(1)).save(product);
    }

    /**
     * Real product with all invalid fields → real validator throws → save is never called.
     * Demonstrates assertThrows and verify(never()).
     */
    @Test
    void addProduct_allInvalidFields_throwsAndNotSaved() {
        Product product = new Product(-1, "", 0.0,
                CategorieBautura.TEA, TipBautura.WATER_BASED);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.addProduct(product)
        );

        // The real validator accumulates all messages
        assertTrue(ex.getMessage().contains("ID invalid!"));
        verify(mockRepo, never()).save(any());
    }

    /**
     * Real product with valid id/name but zero price → only price error raised.
     */
    @Test
    void addProduct_zeroPriceProduct_throwsPriceError() {
        Product product = new Product(2, "Latte", 0.0,
                CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.addProduct(product)
        );

        assertTrue(ex.getMessage().contains("Pret invalid!"));
        verify(mockRepo, never()).save(any());
    }
}
