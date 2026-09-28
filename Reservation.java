public class Reservation {
    private LibraryItem item;
    private User requester;
    private boolean fulfilled;
    private boolean held;

    public Reservation(LibraryItem item, User requester) {
        this.item = item;
        this.requester = requester;
        this.fulfilled = false;
        this.held = false;
    }

    public boolean placeHold() {
        if (fulfilled || held) {
            return false;
        }
        held = true;
        return true;
    }

    public void claimItem() {
        if (held) {
            held = false;
            fulfilled = true;
        }
    }

    public void cancel() {
        held = false;
        fulfilled = true;
    }

    public boolean matches(String itemId) {
        return !fulfilled && item.getId().equalsIgnoreCase(itemId);
    }

    public LibraryItem getItem() {
        return item;
    }

    public User getRequester() {
        return requester;
    }

    public boolean isHeld() {
        return held;
    }
}
