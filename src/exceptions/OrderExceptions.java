package exceptions;

public class OrderExceptions {
  public static class OrderException extends Exception {
    public OrderException(String message) {
      super(message);
    }
  }

  public static class InvalidOrderTableException extends OrderException {
    public InvalidOrderTableException(String message) {
      super(message);
    }
  }

  public static class InvalidMenuItemException extends OrderException {
    public InvalidMenuItemException(String message) {
      super(message);
    }
  }
}
