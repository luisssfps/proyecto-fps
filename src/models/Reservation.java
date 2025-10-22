package models;

import java.time.LocalDateTime;

public class Reservation {
    private Customer customer;
    private Table table;
    private LocalDateTime reservationTime;
    private int numberOfGuests;
    private ReservationStatus status;

    public Reservation(Customer customer, Table table, LocalDateTime reservationTime, int numberOfGuests) {
        this.customer = customer;
        this.table = table;
        this.reservationTime = reservationTime;
        this.numberOfGuests = numberOfGuests;
        this.status = ReservationStatus.PENDING;
    }

    // Getters and Setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "customer=" + customer +
                ", table=" + table +
                ", reservationTime=" + reservationTime +
                ", numberOfGuests=" + numberOfGuests +
                ", status=" + status +
                '}';
    }
}
