package mydrinkshop.service;

/**
 * Lab04 — Integration Testing (top-down incremental, Scenario 1: V <--- S ---> R)
 * ---------------------------------------------------------------------------------
 * STEP 2: Integram V (StocValidator real) R = mock, E = mock
 *
 */

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.domain.Stoc;
import drinkshop.repository.Repository;
import drinkshop.service.StocService;
import drinkshop.service.validator.StocValidator;
import drinkshop.service.validator.ValidationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StocServiceStep2Test {

    @Mock
    private Repository<Integer, Stoc> mockRepo;   // R – mock

    @Test
    @DisplayName("IT2-01 - add(Stoc valid) cu validator real → save() apelat pe R mock")
    void it2_01_add_validStoc_realValidator_saveCalled() {
        StocService service = new StocService(mockRepo, new StocValidator());
        Stoc stoc = new Stoc(1, "zahar", 100, 10);

        assertDoesNotThrow(() -> service.add(stoc));
        verify(mockRepo, times(1)).save(stoc);
    }

    @Test
    @DisplayName("IT2-02 - add(Stoc invalid: id=0) cu validator real → ValidationException, save() NU apelat")
    void it2_02_add_invalidStoc_realValidator_throwsAndSaveNotCalled() {
        StocService service = new StocService(mockRepo, new StocValidator());
        Stoc stocInvalid = new Stoc(0, "zahar", 100, 10);

        assertThrows(ValidationException.class, () -> service.add(stocInvalid));
        verify(mockRepo, never()).save(any());
    }

    @Test
    @DisplayName("IT2-03 - areSuficient() cu mock E returnat de mock R → true")
    void it2_03_areSuficient_mockE_inMockR_returnsTrue() {

        Stoc mockStoc = mock(Stoc.class);
        when(mockStoc.getIngredient()).thenReturn("lapte");
        when(mockStoc.getCantitate()).thenReturn(200.0);
        when(mockRepo.findAll()).thenReturn(List.of(mockStoc));

        StocService service = new StocService(mockRepo, new StocValidator());
        Reteta reteta = new Reteta(1, List.of(new IngredientReteta("lapte", 100)));

        assertTrue(service.areSuficient(reteta));      // 200 >= 100
        verify(mockRepo, atLeastOnce()).findAll();
    }
}
