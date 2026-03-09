package drinkshop.export;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import javafx.scene.control.Alert;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvExporter {
    public static void exportOrders(List<Product> products, List<Order> orders, String path) {
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        try (FileWriter w = new FileWriter(path)) {
            w.write("ID_COMANDA;Produs;Cantitate;Pret_Unitar;Subtotal;Data\n");

            double totalZi = 0.0;
            String dataCurenta = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            for (Order o : orders) {
                for (OrderItem i : o.getItems()) {
                    Product p = productMap.get(i.getProduct().getId());

                    String numeProdus = (p != null) ? p.getNume() : "Produs_Sters";
                    double pretUnitar = (p != null) ? p.getPret() : 0.0;

                    w.write(String.format("%d,%s,%d,%.2f,%.2f,%s\n",
                            o.getId(), numeProdus, i.getQuantity(), pretUnitar, i.getTotal(), dataCurenta));
                }
                totalZi += o.getTotal();
            }

            w.write(",,,,TOTAL ZI," + String.format("%.2f", totalZi) + "\n");

        } catch (IOException e) {
            System.err.println("Eroare la scrierea raportului: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Eroare la scrierea raportului");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}