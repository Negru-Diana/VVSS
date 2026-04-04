package mydrinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.StocService;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * White-Box Tests for StocService.areSuficient(Reteta)
 *
 * CFG Nodes:  1(if reteta==null) | 2(return false) | 3(getIngrediente)
 *             4(if null||empty)  | 5(return false) | 6(for loop)
 *             7(loop body)       | 8(if disp<nec)  | 9(return false)
 *             10(return true)    | 11(EXIT)
 *
 * CC = E - N + 2 = 15 - 12 + 2 = 5   |   Independent paths: P01-P05 (P03 infeasible)
 */
class StocServiceTest {

    private StocService stocService;
    private AbstractRepository<Integer, Stoc> stocRepo;

    @BeforeEach
    void setUp() {
        stocRepo = new AbstractRepository<>() {
            @Override
            protected Integer getId(Stoc entity) {
                return entity.getId();
            }
        };
        stocService = new StocService(stocRepo);
    }

    // -------------------------------------------------------------------------
    // TC01 | SC: nodes {1,2,11} | APC: P01 | DC/CC: Cond01=T
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC01 - reteta=null → false  [SC, DC, CC, DCC, MCC, APC:P01]")
    void TC01_reteta_null_returnsFalse() {
        // path: 1(T) → 2 → 11
        assertFalse(stocService.areSuficient(null));
    }

    // -------------------------------------------------------------------------
    // TC02 | SC: nodes {1,3,4,5,11} | APC: P02 | MCC: Cond01=F, Cond02=T
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC02 - ingrediente=null → false  [SC, DC, CC, DCC, MCC, APC:P02]")
    void TC02_ingrediente_null_returnsFalse() {
        // path: 1(F) → 3 → 4(T) → 5 → 11
        Reteta reteta = new Reteta(1, null);
        assertFalse(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC03 | SC: nodes {1,3,4,5,11} | APC: P02 | MCC: Cond01=F, Cond02=F, Cond03=T
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC03 - ingrediente=[] → false  [SC, DC, CC, DCC, MCC, APC:P02]")
    void TC03_ingrediente_empty_returnsFalse() {
        // path: 1(F) → 3 → 4(T via isEmpty) → 5 → 11
        Reteta reteta = new Reteta(1, new ArrayList<>());
        assertFalse(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC04 | SC: nodes {1,3,4,6,7,8,9,11} | APC: P04 | LC: 1 iteratie
    //       DC/CC: Cond01=F, Cond02=F, Cond03=F, loop=T, Cond05(disp<nec)=T
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC04 - 1 ingredient, stoc insuficient → false  [SC, DC, CC, DCC, MCC, APC:P04, LC:1]")
    void TC04_oneIngredient_insufficientStock_returnsFalse() {
        // stoc disponibil: 50ml lapte
        stocRepo.save(new Stoc(1, "lapte", 50, 0));

        // reteta necesita: 100ml lapte  →  50 < 100  →  insuficient
        Reteta reteta = new Reteta(1,
                List.of(new IngredientReteta("lapte", 100)));

        // path: 1(F) → 3 → 4(F) → 6(T) → 7 → 8(T) → 9 → 11
        assertFalse(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC05 | SC: nodes {1,3,4,6,7,8,6,10,11} | APC: P05 | LC: 1 iteratie
    //       DC/CC: Cond01=F, Cond02=F, Cond03=F, loop=T→F, Cond05=F
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC05 - 1 ingredient, stoc suficient → true  [SC, DC, CC, DCC, MCC, APC:P05, LC:1]")
    void TC05_oneIngredient_sufficientStock_returnsTrue() {
        // stoc disponibil: 200ml lapte
        stocRepo.save(new Stoc(1, "lapte", 200, 0));

        // reteta necesita: 100ml lapte  →  200 >= 100  →  suficient
        Reteta reteta = new Reteta(1,
                List.of(new IngredientReteta("lapte", 100)));

        // path: 1(F) → 3 → 4(F) → 6(T) → 7 → 8(F) → 6(F) → 10 → 11
        assertTrue(stocService.areSuficient(reteta));
    }

    // -------------------------------------------------------------------------
    // TC06 | APC: P05 extins | LC: 2 iteratii
    //       DC/CC: loop=T,T,F | Cond05=F,F
    // -------------------------------------------------------------------------
    @Test
    @DisplayName("TC06 - 2 ingrediente, ambele suficiente → true  [DC, CC, DCC, APC:P05, LC:2]")
    void TC06_twoIngredients_bothSufficient_returnsTrue() {
        // stoc disponibil
        stocRepo.save(new Stoc(1, "lapte", 200, 0));
        stocRepo.save(new Stoc(2, "zahar", 100, 0));

        // reteta necesita: 100ml lapte + 50g zahar  →  ambele suficiente
        List<IngredientReteta> ingrediente = List.of(
                new IngredientReteta("lapte", 100),
                new IngredientReteta("zahar", 50)
        );
        Reteta reteta = new Reteta(1, ingrediente);

        // path: 1(F) → 3 → 4(F) → 6(T) → 7 → 8(F) → 6(T) → 7 → 8(F) → 6(F) → 10 → 11
        assertTrue(stocService.areSuficient(reteta));
    }
}
