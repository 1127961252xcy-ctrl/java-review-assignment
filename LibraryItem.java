// Common item data lives in one abstract parent class.
// Private fields keep the object's state controlled.
public abstract class LibraryItem {
    private String id;
    private String title;
    private String creator;
    private String genre;
    private double rating;
    private String status;

    public LibraryItem(String id, String title, String creator, String genre,
                       double rating, String status) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.genre = genre;
        this.rating = rating;
        this.status = status;
    }

    public abstract String getDetails();

    public boolean isAvailable() {
        return status.equals("AVAILABLE");
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public String getGenre() {
        return genre;
    }

    public double getRating() {
        return rating;
    }

    public String getStatus() {
        return status;
    }
}
