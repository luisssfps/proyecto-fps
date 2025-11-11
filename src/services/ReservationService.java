package services;

import exceptions.ReservationException;
import models.Customer;
import models.Reservation;
import models.ReservationStatus;
import models.Table;
import persistence.DataAccessObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationService {
    private List<Reservation> reservations;
    private final DataAccessObject<Reservation> reservationDao;
    private final TableService tableService;

    public ReservationService(DataAccessObject<Reservation> reservationDao, TableService tableService) {
        this.reservationDao = reservationDao;
        this.tableService = tableService;
        this.reservations = reservationDao.loadAll();
    }

    public Reservation createReservation(Customer customer, LocalDateTime reservationTime, int numberOfGuests) throws ReservationException {
        List<Table> availableTables = tableService.getAvailableTables().stream()
                .filter(table -> table.getCapacity() >= numberOfGuests)
                .collect(Collectors.toList());

        if (availableTables.isEmpty()) {
            throw new ReservationException("No available tables for the selected time and number of guests.");
        }

        Table assignedTable = availableTables.get(0); // Simple assignment, can be improved
        tableService.updateTableAvailability(assignedTable.getTableNumber(), false);

        Reservation newReservation = new Reservation(customer, assignedTable, reservationTime, numberOfGuests);
        newReservation.setStatus(ReservationStatus.CONFIRMED);
        reservations.add(newReservation);
        saveReservations();

        return newReservation;
    }

    public void cancelReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELED);
        tableService.updateTableAvailability(reservation.getTable().getTableNumber(), true);
        saveReservations();
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    private void saveReservations() {
        reservationDao.saveAll(reservations);
    }
}
