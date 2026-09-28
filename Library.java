import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Library {
    private static final String[] ITEM_TYPES = {"BOOK", "MAGAZINE", "MOVIE"};
    private static final String[] USER_TYPES = {"CUSTOMER", "EMPLOYEE", "MASTER"};

    // The library owns the objects stored in these arrays.
    private LibraryItem[] items = new LibraryItem[100];
    private User[] users = new User[100];
    private Loan[] loans = new Loan[100];
    private Reservation[] reservations = new Reservation[100];
    private int itemCount;
    private int userCount;
    private int loanCount;
    private int reservationCount;

    // Read everything first, then build the objects from memory.
    public void loadData(String filename) {
        String[] lines = readLines(filename);
        for (int i = 0; i < lines.length && itemCount < items.length; i++) {
            LibraryItem item = createItem(lines[i]);
            if (item != null) {
                items[itemCount] = item;
                itemCount++;
            }
        }
    }

    public void loadUsers(String filename) {
        String[] lines = readLines(filename);
        for (int i = 0; i < lines.length && userCount < users.length; i++) {
            User user = createUser(lines[i]);
            if (user != null) {
                users[userCount] = user;
                userCount++;
            }
        }
    }

    public LibraryItem[] search(String keyword) {
        int resultCount = 0;
        for (int i = 0; i < itemCount; i++) {
            if (matches(items[i], keyword)) {
                resultCount++;
            }
        }

        LibraryItem[] results = new LibraryItem[resultCount];
        int resultIndex = 0;
        for (int i = 0; i < itemCount; i++) {
            if (matches(items[i], keyword)) {
                results[resultIndex] = items[i];
                resultIndex++;
            }
        }
        return results;
    }

    public boolean checkout(String userId, String itemId, String staffId) {
        User borrower = findUser(userId);
        LibraryItem item = findItem(itemId);
        Employee staff = findEmployee(staffId);
        Reservation heldReservation = findHeldReservation(item, borrower);

        if (borrower == null || item == null || staff == null) {
            return false;
        }
        // Employee use requires approval from a MasterLibrarian.
        if (borrower instanceof Employee && !(staff instanceof MasterLibrarian)) {
            return false;
        }
        if (!item.isAvailable() && heldReservation == null) {
            return false;
        }
        if (!staff.approveLoan(item) || loanCount == loans.length) {
            return false;
        }

        if (heldReservation != null) {
            heldReservation.claimItem();
            item.setStatus("AVAILABLE");
        }
        Loan loan = new Loan(item, borrower);
        if (!loan.processCheckout()) {
            return false;
        }
        loans[loanCount] = loan;
        loanCount++;
        return true;
    }

    public boolean checkin(String itemId) {
        Loan loan = findActiveLoan(itemId);
        if (loan == null) {
            return false;
        }
        loan.processReturn();
        loan.getItem().setStatus("AVAILABLE");
        holdNextReservation(loan.getItem());
        return true;
    }

    public boolean reserve(String userId, String itemId) {
        User requester = findUser(userId);
        LibraryItem item = findItem(itemId);
        if (requester == null || item == null || item.isAvailable()
                || reservationCount == reservations.length) {
            return false;
        }

        Reservation reservation = new Reservation(item, requester);
        Employee approver = findReservationApprover(requester);
        if (approver == null || !approver.approveReservation(reservation)) {
            return false;
        }
        reservations[reservationCount] = reservation;
        reservationCount++;
        return true;
    }

    public boolean repair(String itemId, boolean isFixed) {
        LibraryItem item = findItem(itemId);
        if (item == null || item.getStatus().equals("ON_LOAN")) {
            return false;
        }
        item.setStatus(isFixed ? "AVAILABLE" : "REPAIR");
        if (isFixed) {
            holdNextReservation(item);
        }
        return true;
    }

    public boolean addItem(MasterLibrarian librarian, LibraryItem newItem) {
        if (librarian == null || newItem == null || !librarian.addItem(newItem)
                || findItem(newItem.getId()) != null || itemCount == items.length) {
            return false;
        }
        items[itemCount] = newItem;
        itemCount++;
        return true;
    }

    public User findUser(String userId) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getId().equalsIgnoreCase(userId)) {
                return users[i];
            }
        }
        return null;
    }

    public Employee findEmployee(String userId) {
        User user = findUser(userId);
        return user instanceof Employee ? (Employee) user : null;
    }

    public MasterLibrarian findMasterLibrarian(String userId) {
        User user = findUser(userId);
        return user instanceof MasterLibrarian ? (MasterLibrarian) user : null;
    }

    public int getItemCount() {
        return itemCount;
    }

    private String[] readLines(String filename) {
        int count = countLines(filename);
        String[] lines = new String[count];
        int index = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null && index < lines.length) {
                if (!line.trim().isEmpty()) {
                    lines[index] = line.trim();
                    index++;
                }
            }
        } catch (IOException exception) {
            System.out.println("Unable to read file: " + filename);
        }
        return lines;
    }

    private int countLines(String filename) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    count++;
                }
            }
        } catch (IOException exception) {
            System.out.println("Unable to read file: " + filename);
        }
        return count;
    }

    // The index connects each type code to its matching class.
    private LibraryItem createItem(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length < 8) {
            return null;
        }
        int type = indexOf(ITEM_TYPES, fields[0].toUpperCase());
        try {
            double rating = Double.parseDouble(fields[5]);
            int extraValue = Integer.parseInt(fields[7]);
            if (type == 0) {
                return new Book(fields[1], fields[2], fields[3], fields[4], rating, fields[6], extraValue);
            }
            if (type == 1) {
                return new Magazine(fields[1], fields[2], fields[3], fields[4], rating, fields[6], extraValue);
            }
            if (type == 2) {
                return new Movie(fields[1], fields[2], fields[3], fields[4], rating, fields[6], extraValue);
            }
        } catch (NumberFormatException exception) {
            return null;
        }
        return null;
    }

    private User createUser(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length < 3) {
            return null;
        }
        int type = indexOf(USER_TYPES, fields[0].toUpperCase());
        if (type == 0) {
            return new Customer(fields[1], fields[2]);
        }
        if (type == 1) {
            return new Employee(fields[1], fields[2]);
        }
        if (type == 2) {
            return new MasterLibrarian(fields[1], fields[2]);
        }
        return null;
    }

    private int indexOf(String[] values, String target) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }

    private boolean matches(LibraryItem item, String keyword) {
        String value = keyword.toLowerCase();
        return value.isEmpty() || item.getId().toLowerCase().contains(value)
                || item.getTitle().toLowerCase().contains(value)
                || item.getCreator().toLowerCase().contains(value)
                || item.getGenre().toLowerCase().contains(value)
                || String.valueOf(item.getRating()).contains(value);
    }

    private LibraryItem findItem(String itemId) {
        for (int i = 0; i < itemCount; i++) {
            if (items[i].getId().equalsIgnoreCase(itemId)) {
                return items[i];
            }
        }
        return null;
    }

    private Loan findActiveLoan(String itemId) {
        for (int i = 0; i < loanCount; i++) {
            if (loans[i].matches(itemId)) {
                return loans[i];
            }
        }
        return null;
    }

    private Employee findReservationApprover(User requester) {
        for (int i = 0; i < userCount; i++) {
            if (requester instanceof Employee && users[i] instanceof MasterLibrarian) {
                return (MasterLibrarian) users[i];
            }
            if (!(requester instanceof Employee) && users[i] instanceof Employee) {
                return (Employee) users[i];
            }
        }
        return null;
    }

    private Reservation findHeldReservation(LibraryItem item, User requester) {
        if (item == null || requester == null) {
            return null;
        }
        for (int i = 0; i < reservationCount; i++) {
            Reservation reservation = reservations[i];
            if (reservation.isHeld() && reservation.getItem() == item
                    && reservation.getRequester() == requester) {
                return reservation;
            }
        }
        return null;
    }

    private void holdNextReservation(LibraryItem item) {
        if (!item.isAvailable()) {
            return;
        }
        for (int i = 0; i < reservationCount; i++) {
            if (reservations[i].matches(item.getId())) {
                reservations[i].placeHold();
                item.setStatus("RESERVED");
                return;
            }
        }
    }

}
