package mydrinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BlackBox")
public class ProductServiceTest {

    private ProductService productService;
    private Repository<Integer, Product> fakeRepo;

    @BeforeEach
    void setUp() {
        fakeRepo = new drinkshop.repository.AbstractRepository<Integer, Product>() {
            @Override
            protected Integer getId(Product entity) {
                return entity.getId();
            }
        };
        productService = new ProductService(fakeRepo);
    }

    // CAZURI ECP

    @Test
    @DisplayName("TC01_EC: Adăugare produs valid")
    void testAddProduct_Valid_ECP() {
        // Arrange
        Product p = new Product(1, "Coca-Cola", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

        // Act
        productService.addProduct(p);

        // Assert
        Product savedProduct = productService.findById(1);
        assertNotNull(savedProduct, "Produsul ar fi trebuit să fie adăugat.");
        assertEquals("Coca-Cola", savedProduct.getNume());
        assertEquals(5.0, savedProduct.getPret(), 0.001);
    }

    @Test
    @DisplayName("TC02_EC: Nume prea scurt (vid)")
    void testAddProduct_EmptyName_ECP() {
        // Arrange
        Product p = new Product(2, "", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

        // Act & Assert
        Exception ex = assertThrows(Exception.class, () -> productService.addProduct(p));
    }

    @Test
    @DisplayName("TC09_EC: Nume prea lung (>50 caractere)")
    void testAddProduct_NameTooLong_ECP() {
        // Arrange
        String longName = "M".repeat(100); // Șir de 100 de caractere
        Product p = new Product(3, longName, 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

        // Act & Assert
        assertThrows(Exception.class, () -> productService.addProduct(p));
    }

    @Test
    @DisplayName("TC03_EC & TC04_EC: Preț invalid (negativ sau prea mare)")
    void testAddProduct_InvalidPrice_ECP() {
        // Arrange
        Product negativePriceProduct = new Product(4, "Fanta", -1.9, CategorieBautura.JUICE, TipBautura.BASIC);
        Product hugePriceProduct = new Product(5, "Fanta", 11000.0, CategorieBautura.JUICE, TipBautura.BASIC);

        // Act & Assert
        assertThrows(Exception.class, () -> productService.addProduct(negativePriceProduct), "Prețul < 0 trebuie respins.");
        assertThrows(Exception.class, () -> productService.addProduct(hugePriceProduct), "Prețul > 10000 trebuie respins.");
    }

    // CAZURI BVA

    @Test
    @DisplayName("BVA Valid: Limita minima pentru lungimea numelui (1 caracter)")
    void testAddProduct_ValidNameLength_BVA() {
        // Arrange
        Product p = new Product(6, "M", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

        // Act
        productService.addProduct(p);

        // Assert
        Product saved = productService.findById(6);
        assertNotNull(saved);
        assertEquals(1, saved.getNume().length());
    }

    @Test
    @DisplayName("BVA Valid: Limita maxima pentru lungimea numelui (50 caractere)")
    void testAddProduct_ValidNameLength50_BVA() {
        // Arrange
        Product p = new Product(6, "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM", 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

        // Act
        productService.addProduct(p);

        // Assert
        Product saved = productService.findById(6);
        assertNotNull(saved);
        assertEquals(50, saved.getNume().length());
    }

    @Test
    @DisplayName("TC06_BVA: Limita depășită pentru nume (51 caractere)")
    void testAddProduct_InvalidNameLength51_BVA() {
        // Arrange
        String name51 = "M".repeat(51);
        Product p = new Product(7, name51, 5.0, CategorieBautura.JUICE, TipBautura.BASIC);

        // Act & Assert
        assertThrows(Exception.class, () -> productService.addProduct(p), "Numele cu 51 caractere trebuie respins.");
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.1, 9999.9, 10000.0})
    @DisplayName("TC07, TC08, TC11, TC12_BVA: Limite valide pentru preț")
    void testAddProduct_ValidPrice_BVA(double validPrice) {
        // Arrange
        Product p = new Product(8, "Sprite", validPrice, CategorieBautura.JUICE, TipBautura.BASIC);

        // Act
        productService.addProduct(p);

        // Assert
        Product saved = productService.findById(8);
        assertNotNull(saved);
        assertEquals(validPrice, saved.getPret(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 10000.1})
    @DisplayName("TC09, TC13_BVA: Limite invalide pentru preț (imediat sub 0.0 și peste 10000.0)")
    void testAddProduct_InvalidPrice_BVA(double invalidPrice) {
        // Arrange
        Product p = new Product(9, "Sprite", invalidPrice, CategorieBautura.JUICE, TipBautura.BASIC);

        // Act & Assert
        assertThrows(Exception.class, () -> productService.addProduct(p));
    }
}