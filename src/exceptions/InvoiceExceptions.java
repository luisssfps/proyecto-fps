package exceptions;

public class InvoiceExceptions {
  static public class InvoiceException extends Exception {
    public InvoiceException(String message) {
      super(message);
    }
  }

  static public class InvalidInvoiceOrderStatusException extends InvoiceException {
    public InvalidInvoiceOrderStatusException(String message) {
      super(message);
    }
  }
}