public class Employee extends User {
    public Employee(String id, String name) {
        super(id, name);
    }

    public boolean approveLoan(LibraryItem item) {
        return item != null;
    }

    public boolean approveReservation(Reservation reservation) {
        return reservation != null;
    }
}
