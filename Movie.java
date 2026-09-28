public class Movie extends LibraryItem {
    private int duration;

    public Movie(String id, String title, String creator, String genre,
                 double rating, String status, int duration) {
        super(id, title, creator, genre, rating, status);
        this.duration = duration;
    }

    @Override
    public String getDetails() {
        return String.format("[%s] Movie | %s | Director: %s | Genre: %s | Rating: %.1f | Duration: %d min | Status: %s",
                getId(), getTitle(), getCreator(), getGenre(), getRating(), duration, getStatus());
    }
}
