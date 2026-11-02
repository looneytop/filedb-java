package ru.looneytop.filedb.model;

public class VideoGame {
    private final long id;
    private String title;
    private String developer;
    private Genre genre;
    private Platform platform;
    private ReleaseDate releaseDate;
    private double price;
    private double rating;
    private int playtimeHours;
    private boolean completed;

    public VideoGame(long id, String title, String developer,
                     Genre genre, Platform platform, ReleaseDate releaseDate,
                     double price, double rating, int playtimeHours,
                     boolean completed) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id должен быть больше 0");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название игры не должно быть пустым");
        }
        if (developer == null || developer.isBlank()) {
            throw new IllegalArgumentException("Название разработчика не должно быть пустым");
        }
        if (genre == null || platform == null || releaseDate == null) {
            throw new IllegalArgumentException("Genre, platform or date is null");
        }
        if (price < 0 || !Double.isFinite(price)) {
            throw new IllegalArgumentException("Price is invalid");
        }
        if (rating < 0 || !Double.isFinite(rating) || rating > 10){
            throw new IllegalArgumentException("Rating is invalid");
        }
        if (playtimeHours < 0) {
            throw new IllegalArgumentException("Количество часов не может быть отрицательным");
        }

        this.id = id;
        this.title = title;
        this.developer = developer;
        this.genre = genre;
        this.platform = platform;
        this.releaseDate = releaseDate;
        this.price = price;
        this.rating = rating;
        this.playtimeHours = playtimeHours;
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Genre getGenre() {
        return genre;
    }

    public double getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }

    public int getPlaytimeHours() {
        return playtimeHours;
    }

    public long getId() {
        return id;
    }

    public Platform getPlatform() {
        return platform;
    }

    public ReleaseDate getReleaseDate() {
        return releaseDate;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getTitle() {
        return title;
    }
}
