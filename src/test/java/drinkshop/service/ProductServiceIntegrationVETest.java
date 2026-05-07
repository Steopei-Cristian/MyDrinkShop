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
 * Step 3 – Integration test: real ProductValidator (V) + real Product (E) + mock Repository (R).
 * E and V are now fully real; only the persistence layer stays mocked.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceIntegrationVETest {

    @Mock
    private Repository<Integer, Product> mockRepo;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(mockRepo, new ProductValidator());
    }

    /**
     * Real valid Product → validator passes → repository.save() called exactly once.
     * Demonstrates assert (no exception) and verify (save interaction).
     */
    @Test
    void addProduct_validRealProduct_savedInRepo() {
        Product product = new Product(1, "Cola", 5.0,
                CategorieBautura.JUICE, TipBautura.WATER_BASED);

        assertDoesNotThrow(() -> service.addProduct(product));

        verify(mockRepo, times(1)).save(product);
    }

    /**
     * Real Product with blank name → real validator throws → save never called.
     * Demonstrates assertThrows, message assertion, and verify(never()).
     */
    @Test
    void addProduct_blankName_throwsValidationException() {
        Product product = new Product(2, "   ", 3.0,
                CategorieBautura.TEA, TipBautura.WATER_BASED);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.addProduct(product)
        );

        assertTrue(ex.getMessage().contains("Numele nu poate fi gol!"));
        verify(mockRepo, never()).save(any());
    }

    /**
     * Real Product with negative id → real validator throws → save never called.
     */
    @Test
    void addProduct_negativeId_throwsValidationException() {
        Product product = new Product(-5, "Espresso", 8.0,
                CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.addProduct(product)
        );

        assertTrue(ex.getMessage().contains("ID invalid!"));
        verify(mockRepo, never()).save(any());
    }

    /**
     * Real Product with all invalid fields → real validator collects all errors.
     */
    @Test
    void addProduct_allFieldsInvalid_throwsWithAllMessages() {
        Product product = new Product(0, "", -1.0,
                CategorieBautura.TEA, TipBautura.BASIC);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.addProduct(product)
        );

        assertTrue(ex.getMessage().contains("ID invalid!"));
        assertTrue(ex.getMessage().contains("Numele nu poate fi gol!"));
        assertTrue(ex.getMessage().contains("Pret invalid!"));
        verify(mockRepo, never()).save(any());
    }
}
