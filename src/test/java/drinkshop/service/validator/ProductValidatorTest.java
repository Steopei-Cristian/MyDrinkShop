package drinkshop.service.validator;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductValidatorTest {

    private ProductValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProductValidator();
    }

    @ParameterizedTest(name = "[{index}] id={0}, name=''{1}'', price={2} -> expected result: {5}")
    @CsvSource({
            // valid cases
            "1, Espresso, 35, CLASSIC_COFFEE, BASIC, ''",
            "7, Matcha, 30, SPECIAL_COFFEE, PLANT_BASED, ''",
            "1, Pumpkin-Spice latte, 0.1, ICED_COFFEE, BASIC, ''",
            "1, ab, 25, ICED_COFFEE, BASIC, ''",
            "7, Matcha, 2.0, SPECIAL_COFFEE, PLANT_BASED, ''",
            
            // invalid cases
            "2, Pumpkin-spiced latte, -50, ICED_COFFEE, BASIC, 'Pret invalid!\\n'",
            "3, '', 25, ICED_COFFEE, BASIC, 'Numele nu poate fi gol!\\n'",
            "-1, Pumpkin-spiced latte, -25, ICED_COFFEE, BASIC, 'ID invalid!\\nPret invalid!\\n'",
            "7, Matcha, -80, SPECIAL_COFFEE, PLANT_BASED, 'Pret invalid!\\n'",
            "0, Pumpkin-Spice latte, 25, ICED_COFFEE, BASIC, 'ID invalid!\\n'",
            "1, Pumpkin-Spice latte, 0.0, ICED_COFFEE, BASIC, 'Pret invalid!\\n'",
            "7, Matcha, 0.0, SPECIAL_COFFEE, PLANT_BASED, 'Pret invalid!\\n'"
    })
    void testValidateProduct(int id, String name, double price, CategorieBautura category, TipBautura type, String expectedError) {
        Product product = new Product(id, name == null ? "" : name, price, category, type);
        
        if (expectedError.isEmpty()) {
            assertDoesNotThrow(() -> validator.validate(product));
        } else {
            ValidationException exception = assertThrows(ValidationException.class, () -> validator.validate(product));
            assertEquals(expectedError.replace("\\n", "\n"), exception.getMessage());
        }
    }
}
