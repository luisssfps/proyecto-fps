package services;

import models.MenuItem;
import persistence.DataAccessObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuService {
    private List<MenuItem> menuItems;
    private final DataAccessObject<MenuItem> menuItemDao;

    public MenuService(DataAccessObject<MenuItem> menuItemDao) {
        this.menuItemDao = menuItemDao;
        this.menuItems = menuItemDao.loadAll();
        if (this.menuItems.isEmpty()) {
            initializeDefaultMenu();
        }
    }

    private void initializeDefaultMenu() {
        menuItems.add(new MenuItem("Pizza Margherita", "Classic pizza with tomato and mozzarella", new BigDecimal("12.50"), "Pizza"));
        menuItems.add(new MenuItem("Pasta Carbonara", "Pasta with egg, cheese, and bacon", new BigDecimal("15.00"), "Pasta"));
        menuItems.add(new MenuItem("Tiramisu", "Coffee-flavoured Italian dessert", new BigDecimal("7.50"), "Dessert"));
        saveMenuItems();
    }

    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
        saveMenuItems();
    }

    public List<MenuItem> getMenuItems() {
        return new ArrayList<>(menuItems);
    }

    public Optional<MenuItem> getMenuItemByName(String name) {
        return menuItems.stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public boolean removeMenuItem(String name) {
        boolean removed = menuItems.removeIf(item -> item.getName().equalsIgnoreCase(name));
        if (removed) {
            saveMenuItems();
        }
        return removed;
    }

    private void saveMenuItems() {
        menuItemDao.saveAll(menuItems);
    }
}
