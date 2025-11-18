package facade;



import services.*;
import exceptions.InvoiceExceptions;
import exceptions.OrderExceptions;
import exceptions.ReservationExceptions;
import models.*;
import java.time.LocalDateTime;
import java.util.*;





public class RestaurantFacadeImpl implements RestaurantFacade {


private final MenuService menuService;
private final TableService tableService;
private final ReservationService reservationService;
private final OrderService orderService;
private final InvoiceService invoiceService;


public RestaurantFacadeImpl(MenuService menuService, TableService tableService,
ReservationService reservationService, OrderService orderService,
InvoiceService invoiceService) {
this.menuService = menuService;
this.tableService = tableService;
this.reservationService = reservationService;
this.orderService = orderService;
this.invoiceService = invoiceService;
}



@Override
public List<MenuItem> getMenuItems() {
return menuService.getMenuItems();
}


@Override
public void addMenuItem(MenuItem item) {
menuService.addMenuItem(item);
}


@Override
public boolean removeMenuItem(String name) {
return menuService.removeMenuItem(name);
}



@Override
public List<Table> getAllTables() {
return tableService.getAllTables();
}


@Override
public List<Table> getAvailableTables() {
return tableService.getAvailableTables();
}


@Override
public void setTableAvailability(int tableNumber, boolean available) {
tableService.updateTableAvailability(tableNumber, available);
}


@Override
public List<Reservation> getAllReservations() {
return reservationService.getAllReservations();
}


@Override
public Reservation createReservation(Customer customer, LocalDateTime time, int guests) throws ReservationExceptions.NoAvailableTablesException {
return reservationService.createReservation(customer, time, guests);
}


@Override
public void cancelReservation(Reservation reservation) {
reservationService.cancelReservation(reservation);
}



@Override
public List<Order> getAllOrders() {
return orderService.getAllOrders();
}


@Override
public Order createOrderFromReservation(Reservation reservation) throws OrderExceptions.InvalidOrderTableException {
return orderService.createOrder(reservation.getCustomer(), reservation.getTable().getTableNumber());
}


@Override
public Order createWalkInOrder(Customer customer, int tableNumber) throws OrderExceptions.InvalidOrderTableException {
tableService.updateTableAvailability(tableNumber, false);
try {
return orderService.createOrder(customer, tableNumber);
} catch (OrderExceptions.InvalidOrderTableException e) {
tableService.updateTableAvailability(tableNumber, true);
throw e;
}
}


@Override
public void addItemToOrder(Order order, String menuItemName) throws OrderExceptions.InvalidMenuItemException {
orderService.addItemToOrder(order, menuItemName);
}


@Override
public void updateOrderStatus(Order order, OrderStatus status) {
orderService.updateOrderStatus(order, status);
}



@Override
public Invoice generateInvoice(Order order, PaymentMethod paymentMethod) throws InvoiceExceptions.InvalidInvoiceOrderStatusException {
return invoiceService.generateInvoice(order, paymentMethod);
}
}

