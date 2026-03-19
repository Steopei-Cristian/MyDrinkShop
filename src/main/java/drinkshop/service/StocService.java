package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StocService {

    private final Repository<Integer, Stoc> stocRepo;

    public StocService(Repository<Integer, Stoc> stocRepo) {
        this.stocRepo = stocRepo;
    }

    public List<Stoc> getAll() {
        return stocRepo.findAll();
    }

    public void add(Stoc s) {
        stocRepo.save(s);
    }

    public void update(Stoc s) {
        stocRepo.update(s);
    }

    public void delete(int id) {
        stocRepo.delete(id);
    }

    public boolean areSuficient(Reteta reteta) {
        Map<String, Double> cantitatiDisponibile = stocRepo.findAll().stream()
                .collect(Collectors.groupingBy(s -> s.getIngredient().toLowerCase(),
                        Collectors.summingDouble(Stoc::getCantitate)));

        for (IngredientReteta e : reteta.getIngrediente()) {
            String ingredient = e.getDenumire().toLowerCase();
            double necesar = e.getCantitate();

            double disponibil = cantitatiDisponibile.getOrDefault(ingredient, 0.0);

            if (disponibil < necesar) {
                return false;
            }
        }
        return true;
    }

    public void consuma(Reteta reteta) {
        if (!areSuficient(reteta)) {
            throw new IllegalStateException("Stoc insuficient pentru rețeta.");
        }

        Map<String, List<Stoc>> stocuriIndexate = stocRepo.findAll().stream()
                .collect(Collectors.groupingBy(s -> s.getIngredient().toLowerCase()));

        for (IngredientReteta e : reteta.getIngrediente()) {
            String ingredient = e.getDenumire().toLowerCase();
            double necesar = e.getCantitate();

            List<Stoc> ingredienteStoc = stocuriIndexate.getOrDefault(ingredient, List.of());

            double ramas = necesar;

            for (Stoc s : ingredienteStoc) {
                if (ramas <= 0) break;

                double deScazut = Math.min(s.getCantitate(), ramas);
                s.setCantitate(s.getCantitate() - deScazut);
                ramas -= deScazut;

                stocRepo.update(s);
            }
        }
    }
}