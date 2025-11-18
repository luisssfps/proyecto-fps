package facade;

import models.*;
import exceptions.*;
import java.time.LocalDateTime;
import java.util.List;



public interface RestaurantFacade {



List<MenuItem> getMenuItems();
void addMenuItem(MenuItem item);
boolean removeMenuItem(String name);



List<Table> getAllTables();
List<Table> getAvailableTables();
void setTableAvailability(int tableNumber, boolean available);



List<Reservation> getAllReservations();
Reservation createReservation(Customer customer, LocalDateTime time, int guests) throws ReservationExceptions.NoAvailableTablesException;
void cancelReservation(Reservation reservation);



List<Order> getAllOrders();
Order createOrderFromReservation(Reservation reservation) throws OrderExceptions.InvalidOrderTableException;
Order createWalkInOrder(Customer customer, int tableNumber) throws OrderExceptions.InvalidOrderTableException;
void addItemToOrder(Order order, String menuItemName) throws OrderExceptions.InvalidMenuItemException;
void updateOrderStatus(Order order, OrderStatus status);



Invoice generateInvoice(Order order, PaymentMethod paymentMethod) throws InvoiceExceptions.InvalidInvoiceOrderStatusException;
}