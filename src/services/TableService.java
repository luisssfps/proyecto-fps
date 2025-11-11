package services;

import models.Table;
import persistence.DataAccessObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TableService {
  private List<Table> tables;
  private final DataAccessObject<Table> tableDao;

  public TableService(DataAccessObject<Table> tableDao) {
    this.tableDao = tableDao;
    this.tables = tableDao.loadAll();
    if (this.tables.isEmpty()) {
      initializeDefaultTables();
    }
  }

  private void initializeDefaultTables() {
    tables.add(new Table(1, 4));
    tables.add(new Table(2, 4));
    tables.add(new Table(3, 2));
    tables.add(new Table(4, 2));
    tables.add(new Table(5, 6));
    saveTables();
  }

  public List<Table> getAllTables() {
    return new ArrayList<>(tables);
  }

  public List<Table> getAvailableTables() {
    return tables.stream()
        .filter(Table::isAvailable)
        .collect(Collectors.toList());
  }

  public Optional<Table> getTableByNumber(int tableNumber) {
    return tables.stream()
        .filter(table -> table.getTableNumber() == tableNumber)
        .findFirst();
  }

  public void updateTableAvailability(int tableNumber, boolean isAvailable) {
    getTableByNumber(tableNumber).ifPresent(table -> {
      table.setAvailable(isAvailable);
      saveTables();
    });
  }

  private void saveTables() {
    tableDao.saveAll(tables);
  }
}
