package mydrinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.StocService;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StocService — Scenario 1: V <--- S ---> R
 *
 * S = StocService (class under test)
 * V = StocValidator (mocked)
 * R = Repository<Integer, Stoc> (mocked)
 *
 * Isolation: both V and R are replaced by Mockito mocks.
 */
@ExtendWith(MockitoExtension.class)
class StocServiceMockTest {

    @Mock
    private StocValidator mockValidator;            // V – mock

    @Mock
    private Repository<Integer, Stoc> mockRepo;    // R – mock

    private StocService stocService;               // S – unit under test

    @BeforeEach
    void setUp() {
        stocService = new StocService(mockRepo, mockValidator);
    }

    // -----------------------------------------------------------------------
    // TC01 — add() cu Stoc valid:
    //   assert: nicio exceptie aruncata
    //   verify: validate() apelat 1 data, save() apelat 1 data
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TC01 - add(Stoc valid) → validate() si save() apelate o singura data")
    void TC01_add_validStoc_callsValidateAndSave() {
        Stoc stoc = new Stoc(1, "lapte", 200, 10);

        // mockValidator.validate() nu arunca nimic (comportament implicit)
        assertDoesNotThrow(() -> stocService.add(stoc));

        verify(mockValidator, times(1)).validate(stoc);  // V interogat
        verify(mockRepo, times(1)).save(stoc);           // R interogat
    }

    // -----------------------------------------------------------------------
    // TC02 — add() cu Stoc invalid:
    //   assert: ValidationException aruncata
    //   verify: validate() apelat, save() NU a fost apelat
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TC02 - add(Stoc invalid) → ValidationException, save() NU e apelat")
    void TC02_add_invalidStoc_throwsValidationException_saveNotCalled() {
        Stoc stocInvalid = new Stoc(-1, "", -5, -1);

        doThrow(new ValidationException("Date invalide!"))
                .when(mockValidator).validate(stocInvalid);

        assertThrows(ValidationException.class, () -> stocService.add(stocInvalid));

        verify(mockValidator, times(1)).validate(stocInvalid);
        verify(mockRepo, never()).save(any());   // R nu trebuie atins
    }

    // -----------------------------------------------------------------------
    // TC03 — areSuficient() cu stoc suficient:
    //   assert: returneaza true
    //   verify: findAll() apelat cel putin o data pe R
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TC03 - areSuficient(reteta suficienta) → true, findAll() apelat pe repo")
    void TC03_areSuficient_sufficientStock_returnsTrue_verifyFindAll() {
        Stoc stocLapte = new Stoc(1, "lapte", 200, 0);
        when(mockRepo.findAll()).thenReturn(List.of(stocLapte));

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("lapte", 100)));

        boolean result = stocService.areSuficient(reteta);

        assertTrue(result);
        verify(mockRepo, atLeastOnce()).findAll();
    }

    // -----------------------------------------------------------------------
    // TC04 — areSuficient() cu stoc insuficient:
    //   assert: returneaza false
    //   verify: findAll() apelat cel putin o data pe R
    // -----------------------------------------------------------------------
    @Test
    @DisplayName("TC04 - areSuficient(reteta insuficienta) → false, findAll() apelat pe repo")
    void TC04_areSuficient_insufficientStock_returnsFalse_verifyFindAll() {
        Stoc stocLapte = new Stoc(1, "lapte", 50, 0);
        when(mockRepo.findAll()).thenReturn(List.of(stocLapte));

        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("lapte", 100)));

        boolean result = stocService.areSuficient(reteta);

        assertFalse(result);
        verify(mockRepo, atLeastOnce()).findAll();
    }
}
