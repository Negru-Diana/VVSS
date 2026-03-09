package drinkshop.reports;

import drinkshop.domain.Order;
import drinkshop.repository.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DailyReportService {
    private Repository<Integer, Order> orderRepo;

    public DailyReportService(Repository<Integer, Order> orderRepo) {
        this.orderRepo = orderRepo;
    }

    public double getTotalRevenue() {
        return orderRepo.findAll().stream().mapToDouble(Order::getTotal).sum();
    }

    public int getTotalOrders() {
//        List<Order> orders = StreamSupport.stream(repo.findAll().spliterator(), false)
//                .collect(Collectors.toList());

        return orderRepo.findAll().size();
    }
}