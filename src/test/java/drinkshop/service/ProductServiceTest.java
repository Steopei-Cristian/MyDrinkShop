package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ValidationException;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Step 1 – Unit test for ProductService (S).
 * Both dependencies (Validator<Product> = V, Repository = R) are mocked.
 * Product (E) is a real POJO — no need to mock a simple value object.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private Repository<Integer, Product> mockRepo;

    @Mock
    private Validator<Product> mockValidator;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(mockRepo, mockValidator);
    }

    /**
     * Valid product: validator does not throw → save must be called exactly once.
     * Demonstrates both assert (no exception) and verify (interactions).
     */
    @Test
    void addProduct_validProduct_callsValidateAndSave() {
        Product product = new Product(1, "Cola", 5.0,
                CategorieBautura.JUICE, TipBautura.WATER_BASED);

        // mockValidator does nothing by default (void method, no stub needed)
        assertDoesNotThrow(() -> service.addProduct(product));

        verify(mockValidator, times(1)).validate(product);
        verify(mockRepo, times(1)).save(product);
    }

    /**
     * Validator throws → save must never be called.
     * Demonstrates assertThrows and verify(never()).
     */
    @Test
    void addProduct_validatorThrows_doesNotSave() {
        Product product = new Product(1, "Cola", 5.0,
                CategorieBautura.JUICE, TipBautura.WATER_BASED);
        doThrow(new ValidationException("ID invalid!\n"))
                .when(mockValidator).validate(product);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> service.addProduct(product)
        );

        assertTrue(ex.getMessage().contains("ID invalid!"));
        verify(mockValidator, times(1)).validate(product);
        verify(mockRepo, never()).save(any());
    }

    /**
     * getAllProducts delegates to the repository.
     */
    @Test
    void getAllProducts_delegatesToRepository() {
        when(mockRepo.findAll()).thenReturn(List.of());

        List<Product> result = service.getAllProducts();

        assertNotNull(result);
        verify(mockRepo, times(1)).findAll();
    }

    /**
     * deleteProduct delegates delete call to the repository.
     */
    @Test
    void deleteProduct_callsRepositoryDelete() {
        service.deleteProduct(42);

        verify(mockRepo, times(1)).delete(42);
    }
}
