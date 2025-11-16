package services;

import models.*;
import persistence.DataAccessObject;
import exceptions.OrderExceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderService {
    private List<Order> orders;
    private final DataAccessObject<Order> orderDao;
    private final MenuService menuService;
    private final TableService tableService;

    public OrderService(DataAccessObject<Order> orderDao, MenuService menuService, TableService tableService) {
        this.orderDao = orderDao;
        this.menuService = menuService;
        this.tableService = tableService;
        this.orders = orderDao.loadAll();
    }

    public Order createOrder(Customer customer, int tableNumber) throws OrderExceptions.InvalidOrderTableException {
        Optional<Table> tableOpt = tableService.getTableByNumber(tableNumber);
        if (tableOpt.isEmpty() || tableOpt.get().isAvailable()) {
            throw new OrderExceptions.InvalidOrderTableException("Cannot create order: Table is not occupied or does not exist.");
        }

        Order newOrder = new Order(customer, tableOpt.get());
        orders.add(newOrder);
        saveOrders();
        return newOrder;
    }

    public void addItemToOrder(Order order, String itemName) throws OrderExceptions.InvalidMenuItemException {
        Optional<MenuItem> menuItemOpt = menuService.getMenuItemByName(itemName);
        if (menuItemOpt.isEmpty()) {
            throw new OrderExceptions.InvalidMenuItemException("Menu item not found: " + itemName);
        }

        order.addItem(menuItemOpt.get());
        saveOrders();
    }

    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        saveOrders();
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    private void saveOrders() {
        orderDao.saveAll(orders);
    }
}