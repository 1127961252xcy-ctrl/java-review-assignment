public class Book extends LibraryItem {
    private int pages;

    public Book(String id, String title, String creator, String genre,
                double rating, String status, int pages) {
        super(id, title, creator, genre, rating, status);
        this.pages = pages;
    }

    @Override
    public String getDetails() {
        return String.format("[%s] Book | %s | Author: %s | Genre: %s | Rating: %.1f | Pages: %d | Status: %s",
                getId(), getTitle(), getCreator(), getGenre(), getRating(), pages, getStatus());
    }
}
