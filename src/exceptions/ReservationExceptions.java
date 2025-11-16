package exceptions;

public class ReservationExceptions {
  public static class ReservationException extends Exception {
    public ReservationException(String message) {
      super(message);
    }
  }

  public static class NoAvailableTablesException extends ReservationException {
    public NoAvailableTablesException(String message) {
      super(message);
    }
  }
}
