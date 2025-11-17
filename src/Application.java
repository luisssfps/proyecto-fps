import models.*;
import persistence.DataAccessObject;
import persistence.SerializableDao;
import services.*;
import exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Application {

  private static final String DATA_PATH = "./data/";

  public static void main(String[] args) {
    DataAccessObject<MenuItem> menuItemDao = new SerializableDao<>(DATA_PATH + "menu.ser", MenuItem.class);
    DataAccessObject<Table> tableDao = new SerializableDao<>(DATA_PATH + "tables.ser", Table.class);
    DataAccessObject<Reservation> reservationDao = new SerializableDao<>(DATA_PATH + "reservations.ser",
        Reservation.class);
    DataAccessObject<Order> orderDao = new SerializableDao<>(DATA_PATH + "orders.ser", Order.class);
    DataAccessObject<Invoice> invoiceDao = new SerializableDao<>(DATA_PATH + "invoices.ser", Invoice.class);

    MenuService menuService = new MenuService(menuItemDao);
    TableService tableService = new TableService(tableDao);
    ReservationService reservationService = new ReservationService(reservationDao, tableService);
    OrderService orderService = new OrderService(orderDao, menuService, tableService);
    InvoiceService invoiceService = new InvoiceService(invoiceDao, tableService);

    startCLI(menuService, tableService, reservationService, orderService, invoiceService);
  }

  private static void startCLI(MenuService menuService, TableService tableService,
      ReservationService reservationService, OrderService orderService, InvoiceService invoiceService) {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println("\n--- Restaurant FPS Management System ---");
      System.out.println("1. Gestionar menú");
      System.out.println("2. Gestionar reservaciones");
      System.out.println("3. Gestionar órdenes");
      System.out.println("4. Salir");

      int choice = promptInt(scanner, "Seleccione una opción: ");

      switch (choice) {
        case 1 -> manageMenu(scanner, menuService);
        case 2 -> manageReservations(scanner, reservationService, tableService);
        case 3 -> manageOrders(scanner, orderService, reservationService, tableService, menuService, invoiceService);
        case 4 -> {
          System.out.println("Hasta pronto!");
          return;
        }
        default -> System.out.println("Opción inválida. Intente nuevamente.");
      }
    }
  }

  private static void manageMenu(Scanner scanner, MenuService menuService) {
    while (true) {
      System.out.println("\n--- Gestión de menú ---");
      System.out.println("1. Ver menú");
      System.out.println("2. Agregar elemento");
      System.out.println("3. Eliminar elemento");
      System.out.println("4. Volver al menú principal");

      int choice = promptInt(scanner, "Seleccione una opción: ");

      switch (choice) {
        case 1 -> {
          List<MenuItem> menuItems = menuService.getMenuItems();
          if (menuItems.isEmpty()) {
            System.out.println("El menú está vacío.");
          } else {
            menuItems.forEach(item -> System.out.println("- " + item));
          }
        }
        case 2 -> addMenuItem(scanner, menuService);
        case 3 -> removeMenuItem(scanner, menuService);
        case 4 -> {
          return;
        }
        default -> System.out.println("Opción inválida. Intente nuevamente.");
      }
    }
  }

  private static void addMenuItem(Scanner scanner, MenuService menuService) {
    String name = promptNonEmptyString(scanner, "Nombre del platillo: ");
    String description = promptNonEmptyString(scanner, "Descripción: ");
    BigDecimal price = promptBigDecimal(scanner, "Precio: ");
    String category = promptNonEmptyString(scanner, "Categoría: ");

    menuService.addMenuItem(new MenuItem(name, description, price, category));
    System.out.println("Elemento agregado al menú.");
  }

  private static void removeMenuItem(Scanner scanner, MenuService menuService) {
    List<MenuItem> items = menuService.getMenuItems();
    MenuItem item = chooseFromList(scanner, items, m -> m.getName() + " - " + m.getCategory(),
        "Seleccione el platillo a eliminar (0 para cancelar):");
    if (item == null) {
      return;
    }

    if (menuService.removeMenuItem(item.getName())) {
      System.out.println("Elemento eliminado del menú.");
    } else {
      System.out.println("No se pudo eliminar el elemento seleccionado.");
    }
  }

  private static void manageReservations(Scanner scanner, ReservationService reservationService,
      TableService tableService) {
    while (true) {
      System.out.println("\n--- Gestión de reservaciones ---");
      System.out.println("1. Ver reservaciones");
      System.out.println("2. Crear reservación");
      System.out.println("3. Cancelar reservación");
      System.out.println("4. Volver al menú principal");

      int choice = promptInt(scanner, "Seleccione una opción: ");

      switch (choice) {
        case 1 -> reservationService.getAllReservations()
            .forEach(reservation -> System.out.println("- " + reservation));
        case 2 -> createReservation(scanner, reservationService, tableService);
        case 3 -> cancelReservation(scanner, reservationService);
        case 4 -> {
          return;
        }
        default -> System.out.println("Opción inválida. Intente nuevamente.");
      }
    }
  }

  private static void createReservation(Scanner scanner, ReservationService reservationService,
      TableService tableService) {
    try {
      String customerName = promptNonEmptyString(scanner, "Nombre del cliente: ");
      String contactInfo = promptNonEmptyString(scanner, "Información de contacto: ");
      Customer customer = new Customer(customerName, contactInfo);

      int guests = promptInt(scanner, "Número de comensales: ");
      if (guests <= 0) {
        System.out.println("Debe ingresar un número positivo de comensales.");
        return;
      }

      LocalDateTime reservationTime = promptDateTime(scanner,
          "Fecha y hora de la reservación (YYYY-MM-DDTHH:MM): ");

      List<Table> availableTables = tableService.getAvailableTables().stream()
          .filter(table -> table.getCapacity() >= guests)
          .collect(Collectors.toList());

      if (availableTables.isEmpty()) {
        System.out.println("No hay mesas disponibles para la cantidad de comensales indicada.");
        return;
      }

      System.out.println("Mesas disponibles sugeridas:");
      availableTables.forEach(table -> System.out.println("- Mesa " + table.getTableNumber() + " (capacidad: "
          + table.getCapacity() + ")"));

      Reservation newReservation = reservationService.createReservation(customer, reservationTime, guests);
      System.out.println("Reservación creada: " + newReservation);
    } catch (ReservationExceptions.NoAvailableTablesException e) {
      System.out.println("Error al crear la reservación: " + e.getMessage());
    } catch (DateTimeParseException e) {
      System.out.println("Formato de fecha/hora inválido. Use YYYY-MM-DDTHH:MM.");
    }
  }

  private static void cancelReservation(Scanner scanner, ReservationService reservationService) {
    List<Reservation> reservations = reservationService.getAllReservations();
    Reservation reservation = chooseFromList(scanner, reservations, r ->
        String.format("%s | Mesa %d | %s | Estado: %s", r.getCustomer().getName(),
            r.getTable().getTableNumber(), r.getReservationTime(), r.getStatus()),
        "Seleccione la reservación a cancelar (0 para cancelar):");

    if (reservation == null) {
      return;
    }

    if (reservation.getStatus() == ReservationStatus.CANCELED) {
      System.out.println("La reservación ya está cancelada.");
      return;
    }

    reservationService.cancelReservation(reservation);
    System.out.println("Reservación cancelada correctamente.");
  }

  private static void manageOrders(Scanner scanner, OrderService orderService,
      ReservationService reservationService, TableService tableService, MenuService menuService,
      InvoiceService invoiceService) {
    while (true) {
      System.out.println("\n--- Gestión de órdenes ---");
      System.out.println("1. Ver órdenes");
      System.out.println("2. Crear orden");
      System.out.println("3. Agregar platillo a orden");
      System.out.println("4. Actualizar estado de orden");
      System.out.println("5. Generar factura");
      System.out.println("6. Volver al menú principal");

      int choice = promptInt(scanner, "Seleccione una opción: ");

      switch (choice) {
        case 1 -> orderService.getAllOrders().forEach(order ->
            System.out.println(String.format("- Mesa %d | Cliente: %s | Estado: %s | Total: %s",
                order.getTable().getTableNumber(), order.getCustomer().getName(), order.getStatus(), order.getTotal())));
        case 2 -> createOrder(scanner, orderService, reservationService, tableService);
        case 3 -> addItemToOrder(scanner, orderService, menuService);
        case 4 -> updateOrderStatus(scanner, orderService);
        case 5 -> generateInvoice(scanner, orderService, invoiceService);
        case 6 -> {
          return;
        }
        default -> System.out.println("Opción inválida. Intente nuevamente.");
      }
    }
  }

  private static void createOrder(Scanner scanner, OrderService orderService,
      ReservationService reservationService, TableService tableService) {
    System.out.println("\nCrear orden");
    System.out.println("1. Basada en reservación existente");
    System.out.println("2. Cliente sin reservación");

    int option = promptInt(scanner, "Seleccione una opción: ");

    switch (option) {
      case 1 -> createOrderFromReservation(scanner, orderService, reservationService);
      case 2 -> createWalkInOrder(scanner, orderService, tableService);
      default -> System.out.println("Opción inválida.");
    }
  }

  private static void createOrderFromReservation(Scanner scanner, OrderService orderService,
      ReservationService reservationService) {
    List<Reservation> confirmedReservations = reservationService.getAllReservations().stream()
        .filter(reservation -> reservation.getStatus() == ReservationStatus.CONFIRMED)
        .collect(Collectors.toList());

    Reservation selected = chooseFromList(scanner, confirmedReservations, r ->
        String.format("Mesa %d | %s | %s", r.getTable().getTableNumber(), r.getCustomer().getName(),
            r.getReservationTime()),
        "Seleccione la reservación para crear la orden (0 para cancelar):");

    if (selected == null) {
      return;
    }

    boolean existingOrder = orderService.getAllOrders().stream()
        .anyMatch(order -> order.getTable().getTableNumber() == selected.getTable().getTableNumber()
            && order.getStatus() != OrderStatus.CANCELED);

    if (existingOrder) {
      System.out.println("Ya existe una orden activa para la mesa seleccionada.");
      return;
    }

    try {
      Order order = orderService.createOrder(selected.getCustomer(), selected.getTable().getTableNumber());
      System.out.println("Orden creada: " + order);
    } catch (OrderExceptions.InvalidOrderTableException e) {
      System.out.println("No se pudo crear la orden: " + e.getMessage());
    }
  }

  private static void createWalkInOrder(Scanner scanner, OrderService orderService, TableService tableService) {
    String customerName = promptNonEmptyString(scanner, "Nombre del cliente: ");
    String contactInfo = promptNonEmptyString(scanner, "Información de contacto: ");
    Customer customer = new Customer(customerName, contactInfo);

    List<Table> availableTables = tableService.getAvailableTables();
    Table table = chooseFromList(scanner, availableTables,
        t -> String.format("Mesa %d (capacidad %d)", t.getTableNumber(), t.getCapacity()),
        "Seleccione la mesa para el cliente (0 para cancelar):");

    if (table == null) {
      return;
    }

    tableService.updateTableAvailability(table.getTableNumber(), false);

    try {
      Order order = orderService.createOrder(customer, table.getTableNumber());
      System.out.println("Orden creada: " + order);
    } catch (OrderExceptions.InvalidOrderTableException e) {
      System.out.println("No se pudo crear la orden: " + e.getMessage());
      tableService.updateTableAvailability(table.getTableNumber(), true);
    }
  }

  private static void addItemToOrder(Scanner scanner, OrderService orderService, MenuService menuService) {
    List<Order> orders = orderService.getAllOrders();
    Order order = chooseFromList(scanner, orders, o ->
        String.format("Mesa %d | Cliente: %s | Estado: %s | Total actual: %s",
            o.getTable().getTableNumber(), o.getCustomer().getName(), o.getStatus(), o.getTotal()),
        "Seleccione la orden a actualizar (0 para cancelar):");

    if (order == null) {
      return;
    }

    List<MenuItem> menuItems = menuService.getMenuItems();
    if (menuItems.isEmpty()) {
      System.out.println("No hay elementos en el menú para agregar.");
      return;
    }

    MenuItem menuItem = chooseFromList(scanner, menuItems,
        item -> String.format("%s (%s) - %s", item.getName(), item.getCategory(), item.getPrice()),
        "Seleccione el platillo a agregar (0 para cancelar):");

    if (menuItem == null) {
      return;
    }

    try {
      orderService.addItemToOrder(order, menuItem.getName());
      System.out.println("Platillo agregado a la orden.");
    } catch (OrderExceptions.InvalidMenuItemException e) {
      System.out.println("Error al agregar el platillo: " + e.getMessage());
    }
  }

  private static void updateOrderStatus(Scanner scanner, OrderService orderService) {
    List<Order> orders = orderService.getAllOrders();
    Order order = chooseFromList(scanner, orders, o ->
        String.format("Mesa %d | Cliente: %s | Estado actual: %s",
            o.getTable().getTableNumber(), o.getCustomer().getName(), o.getStatus()),
        "Seleccione la orden a actualizar (0 para cancelar):");

    if (order == null) {
      return;
    }

    OrderStatus newStatus = promptEnumSelection(scanner, OrderStatus.class, "Seleccione el nuevo estado:");
    orderService.updateOrderStatus(order, newStatus);
    System.out.println("Estado de la orden actualizado a " + newStatus + ".");
  }

  private static void generateInvoice(Scanner scanner, OrderService orderService, InvoiceService invoiceService) {
    List<Order> invoiceableOrders = orderService.getAllOrders().stream()
        .filter(order -> order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.SERVED)
        .collect(Collectors.toList());

    Order order = chooseFromList(scanner, invoiceableOrders, o ->
        String.format("Mesa %d | Cliente: %s | Total: %s | Estado: %s",
            o.getTable().getTableNumber(), o.getCustomer().getName(), o.getTotal(), o.getStatus()),
        "Seleccione la orden para generar factura (0 para cancelar):");

    if (order == null) {
      return;
    }

    PaymentMethod paymentMethod = promptEnumSelection(scanner, PaymentMethod.class,
        "Seleccione el método de pago:");

    try {
      Invoice invoice = invoiceService.generateInvoice(order, paymentMethod);
      System.out.println("Factura generada: " + invoice);
    } catch (InvoiceExceptions.InvalidInvoiceOrderStatusException e) {
      System.out.println("No se pudo generar la factura: " + e.getMessage());
    }
  }

  private static int promptInt(Scanner scanner, String prompt) {
    while (true) {
      System.out.print(prompt);
      String input = scanner.nextLine();
      try {
        return Integer.parseInt(input.trim());
      } catch (NumberFormatException e) {
        System.out.println("Ingrese un número válido.");
      }
    }
  }

  private static BigDecimal promptBigDecimal(Scanner scanner, String prompt) {
    while (true) {
      System.out.print(prompt);
      String input = scanner.nextLine();
      try {
        return new BigDecimal(input.trim());
      } catch (NumberFormatException e) {
        System.out.println("Ingrese un monto válido.");
      }
    }
  }

  private static String promptNonEmptyString(Scanner scanner, String prompt) {
    while (true) {
      System.out.print(prompt);
      String value = scanner.nextLine().trim();
      if (!value.isEmpty()) {
        return value;
      }
      System.out.println("Este campo no puede quedar vacío.");
    }
  }

  private static LocalDateTime promptDateTime(Scanner scanner, String prompt) {
    while (true) {
      System.out.print(prompt);
      String value = scanner.nextLine();
      try {
        return LocalDateTime.parse(value.trim());
      } catch (DateTimeParseException e) {
        System.out.println("Formato inválido. Use YYYY-MM-DDTHH:MM.");
      }
    }
  }

  private static <T> T chooseFromList(Scanner scanner, List<T> options, Function<T, String> labelProvider,
      String header) {
    if (options == null || options.isEmpty()) {
      System.out.println("No hay opciones disponibles.");
      return null;
    }

    System.out.println(header);
    for (int i = 0; i < options.size(); i++) {
      System.out.printf("%d. %s%n", i + 1, labelProvider.apply(options.get(i)));
    }
    System.out.println("0. Cancelar");

    while (true) {
      int selection = promptInt(scanner, "Seleccione una opción: ");
      if (selection == 0) {
        return null;
      }
      if (selection >= 1 && selection <= options.size()) {
        return options.get(selection - 1);
      }
      System.out.println("Opción inválida. Intente nuevamente.");
    }
  }

  private static <E extends Enum<E>> E promptEnumSelection(Scanner scanner, Class<E> enumClass, String header) {
    E[] values = enumClass.getEnumConstants();
    System.out.println(header);
    for (int i = 0; i < values.length; i++) {
      System.out.printf("%d. %s%n", i + 1, values[i].name());
    }

    while (true) {
      int selection = promptInt(scanner, "Seleccione una opción: ");
      if (selection >= 1 && selection <= values.length) {
        return values[selection - 1];
      }
      System.out.println("Opción inválida. Intente nuevamente.");
    }
  }
}
