package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Customer customer;
    private Table table;
    private List<MenuItem> items;
    private OrderStatus status;

    public Order(Customer customer, Table table) {
        this.customer = customer;
        this.table = table;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
    }

    public void addItem(MenuItem item) {
        items.add(item);
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(MenuItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters and Setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "customer=" + customer +
                ", table=" + table +
                ", items=" + items +
                ", status=" + status +
                '}';
    }
}
