package drinkshop.service.stoc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import drinkshop.domain.*;
import drinkshop.service.StocService;
import drinkshop.repository.file.FileStocRepository;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.io.File;

class StocServiceWBT {
    private StocService stocService;
    private FileStocRepository stocRepo;

    @BeforeEach
    void setUp() {
        File file = new File("test_stoc.txt");
        if (file.exists()) file.delete();

        stocRepo = new FileStocRepository("test_stoc.txt");
        stocService = new StocService(stocRepo);
    }

    @Test
    void F02_TC01_ConsumNormal() {
        // Cafea 30 solicitat, stoc disponibil 50
        stocRepo.save(new Stoc(1, "Cafea", 50, 5));
        IngredientReteta ir = new IngredientReteta("Cafea", 30.0);
        Reteta reteta = new Reteta(101, List.of(ir));

        assertDoesNotThrow(() -> stocService.consuma(reteta));

        double ramas = stocRepo.findAll().get(0).getCantitate();
        assertEquals(20.0, ramas, "Ar trebui să rămână 20 unități (50-30)");
    }

    @Test
    void F02_TC02_StocInsuficient() {
        // Cafea 10 solicitat, stoc disponibil doar 5
        stocRepo.save(new Stoc(1, "Cafea", 5, 2));
        IngredientReteta ir = new IngredientReteta("Cafea", 10.0);
        Reteta reteta = new Reteta(102, List.of(ir));

        assertThrows(IllegalStateException.class, () -> stocService.consuma(reteta));
    }

    @Test
    void F02_TC03_StocFragmentat() {
        // Lapte 70 solicitat, stoc 30 + 50 (total 80)
        stocRepo.save(new Stoc(1, "Lapte", 30, 5));
        stocRepo.save(new Stoc(2, "Lapte", 50, 5));
        IngredientReteta ir = new IngredientReteta("Lapte", 70.0);
        Reteta reteta = new Reteta(103, List.of(ir));

        assertDoesNotThrow(() -> stocService.consuma(reteta));

        double totalRamas = stocRepo.findAll().stream()
                .filter(s -> s.getIngredient().equalsIgnoreCase("Lapte"))
                .mapToDouble(Stoc::getCantitate).sum();
        assertEquals(10.0, totalRamas, "Totalul ar trebui să fie 10 (80-70)");
    }

    @Test
    void F02_TC04_RetetaGoala() {
        // Reteta: []
        Reteta retetaGoala = new Reteta(104, new ArrayList<>());
        assertDoesNotThrow(() -> stocService.consuma(retetaGoala));
    }

    @Test
    void F02_TC05_IngredientLipsa() {
        // Matcha: 10, stoc: null (ingredientul nu există deloc în repo)
        IngredientReteta ir = new IngredientReteta("Matcha", 10.0);
        Reteta reteta = new Reteta(105, List.of(ir));

        assertThrows(IllegalStateException.class, () -> stocService.consuma(reteta));
    }

    @Test
    void F02_TC06_VerificareMetodeCRUD() {
        // Acoperă metodele getAll, add, update, delete care apar cu roșu
        Stoc s = new Stoc(99, "Test", 10, 1);

        stocService.add(s);
        assertFalse(stocService.getAll().isEmpty());

        s.setCantitate(15);
        stocService.update(s);

        stocService.delete(99);
    }

    @Test
    void F02_TC07_BreakConditionFullCoverage() {
        // 'ramas <= 0' (Path P04)
        stocRepo.save(new Stoc(1, "Apa", 50, 10));
        stocRepo.save(new Stoc(2, "Apa", 50, 10)); // Al doilea lot nu trebuie consumat

        IngredientReteta ir = new IngredientReteta("Apa", 20.0);
        Reteta reteta = new Reteta(200, List.of(ir));

        stocService.consuma(reteta);

        // Verificam ca al doilea lot a ramas intact (confirma executarea 'break')
        assertEquals(50.0, stocRepo.findAll().get(1).getCantitate());
    }
}