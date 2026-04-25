package mydrinkshop.service;

/**
 * Lab04 — Integration Testing (top-down incremental, Scenario 1: V <--- S ---> R)
 * ---------------------------------------------------------------------------------
 * STEP 4: Integram E (Stoc real)
 *         V = real, R = real, E = real — niciun mock
 *
 * Scop: testare end-to-end completa. Toate componentele sunt reale.
 *       Un defect gasit aici (dar nu in Step 2/3) indica o problema
 *       in interactiunea dintre toate trei componentele impreuna.
 */

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.StocService;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StocServiceStep4Test {

    private AbstractRepository<Integer, Stoc> buildInMemoryRepo() {
        return new AbstractRepository<>() {
            @Override
            protected Integer getId(Stoc entity) {
                return entity.getId();
            }
        };
    }

    @Test
    @DisplayName("IT4-01 - add(real Stoc valid) → salvat corect in repo real")
    void it4_01_add_realStoc_savedInRepo() {
        AbstractRepository<Integer, Stoc> realRepo = buildInMemoryRepo();
        StocService service = new StocService(realRepo, new StocValidator());

        Stoc stoc = new Stoc(1, "lapte", 500, 50);
        service.add(stoc);

        assertEquals(stoc, realRepo.findOne(1));
        assertEquals("lapte", realRepo.findOne(1).getIngredient());
    }

    @Test
    @DisplayName("IT4-02 - areSuficient() cu real E in real R → true")
    void it4_02_areSuficient_realE_realR_returnsTrue() {
        AbstractRepository<Integer, Stoc> realRepo = buildInMemoryRepo();
        realRepo.save(new Stoc(1, "lapte", 500, 0));
        realRepo.save(new Stoc(2, "zahar", 300, 0));

        StocService service = new StocService(realRepo, new StocValidator());
        Reteta reteta = new Reteta(1, List.of(
                new IngredientReteta("lapte", 200),
                new IngredientReteta("zahar", 100)
        ));

        assertTrue(service.areSuficient(reteta));
    }

    @Test
    @DisplayName("IT4-03 - add(Stoc cu cantitate < stocMinim) → ValidationException, E nu e salvat")
    void it4_03_add_cantitateSubMinim_throwsAndNotSaved() {
        AbstractRepository<Integer, Stoc> realRepo = buildInMemoryRepo();
        StocService service = new StocService(realRepo, new StocValidator());

        Stoc stocInvalid = new Stoc(3, "lapte", 5, 10); // 5 < stocMinim 10

        assertThrows(ValidationException.class, () -> service.add(stocInvalid));
        assertNull(realRepo.findOne(3));
    }
}
