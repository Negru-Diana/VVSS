package mydrinkshop.service;

/**
 * Lab04 — Integration Testing (top-down incremental, Scenario 1: V <--- S ---> R)
 * ---------------------------------------------------------------------------------
 * STEP 3: Integram R (AbstractRepository in-memory real)
 *         V = real (deja integrat la Step 2), E = mock
 *
 * Scop: verificam ca S + V real colaboreaza corect cu repo-ul real,
 *       folosind mock pentru E (Stoc) ca sa controlam exact datele din repo.
 */

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.StocService;
import drinkshop.service.validator.StocValidator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StocServiceStep3Test {

    private AbstractRepository<Integer, Stoc> buildInMemoryRepo() {
        return new AbstractRepository<>() {
            @Override
            protected Integer getId(Stoc entity) {
                return entity.getId();
            }
        };
    }

    @Test
    @DisplayName("IT3-01 - areSuficient() cu mock E salvat in real R → true")
    void it3_01_areSuficient_mockE_storedInRealRepo_returnsTrue() {
        AbstractRepository<Integer, Stoc> realRepo = buildInMemoryRepo();

        // E (Stoc) – mock salvat in real R
        // getId() necesar pentru ca repo-ul cheama getId() la save()
        Stoc mockStoc = mock(Stoc.class);
        when(mockStoc.getId()).thenReturn(1);
        when(mockStoc.getIngredient()).thenReturn("lapte");
        when(mockStoc.getCantitate()).thenReturn(300.0);
        realRepo.save(mockStoc);

        StocService service = new StocService(realRepo, new StocValidator());
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("lapte", 200)));

        assertTrue(service.areSuficient(reteta));      // 300 >= 200
    }

    @Test
    @DisplayName("IT3-02 - areSuficient() cu mock E insuficient in real R → false")
    void it3_02_areSuficient_mockE_insufficientInRealRepo_returnsFalse() {
        AbstractRepository<Integer, Stoc> realRepo = buildInMemoryRepo();

        Stoc mockStoc = mock(Stoc.class);
        when(mockStoc.getId()).thenReturn(1);
        when(mockStoc.getIngredient()).thenReturn("lapte");
        when(mockStoc.getCantitate()).thenReturn(50.0);
        realRepo.save(mockStoc);

        StocService service = new StocService(realRepo, new StocValidator());
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("lapte", 200)));

        assertFalse(service.areSuficient(reteta));     // 50 < 200
    }

    @Test
    @DisplayName("IT3-03 - add(Stoc valid) cu real V si real R → entitate gasita in repo")
    void it3_03_add_validStoc_savedInRealRepo() {
        AbstractRepository<Integer, Stoc> realRepo = buildInMemoryRepo();
        StocService service = new StocService(realRepo, new StocValidator());

        Stoc stoc = new Stoc(5, "cafea", 200, 20);
        service.add(stoc);

        assertNotNull(realRepo.findOne(5));
    }
}
