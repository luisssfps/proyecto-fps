package services;

import models.Invoice;
import models.Order;
import models.OrderStatus;
import models.PaymentMethod;
import persistence.DataAccessObject;
import exceptions.InvoiceExceptions;

import java.util.ArrayList;
import java.util.List;

public class InvoiceService {
  private List<Invoice> invoices;
  private final DataAccessObject<Invoice> invoiceDao;
  private final TableService tableService;

  public InvoiceService(DataAccessObject<Invoice> invoiceDao, TableService tableService) {
    this.invoiceDao = invoiceDao;
    this.tableService = tableService;
    this.invoices = invoiceDao.loadAll();
  }

  public Invoice generateInvoice(Order order, PaymentMethod paymentMethod) throws InvoiceExceptions.InvalidInvoiceOrderStatusException {
    if (order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.SERVED) {
      throw new InvoiceExceptions.InvalidInvoiceOrderStatusException("Cannot generate invoice for an order that is not completed or served.");
    }

    Invoice newInvoice = new Invoice(order, paymentMethod);
    invoices.add(newInvoice);
    saveInvoices();

    // Free up the table after payment
    tableService.updateTableAvailability(order.getTable().getTableNumber(), true);

    return newInvoice;
  }

  public List<Invoice> getAllInvoices() {
    return new ArrayList<>(invoices);
  }

  private void saveInvoices() {
    invoiceDao.saveAll(invoices);
  }
}