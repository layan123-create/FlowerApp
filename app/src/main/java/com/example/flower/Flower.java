package com.example.flower;

public class Flower {

    private String id;
    private String name;
    private String categoryId;
    private String category;
    private String description;
    private double price;
    private String imageUrl;
    private int stock;
    private double rating;
    private boolean favorite;
    private int quantity;
    private boolean isPopular;
    private boolean isFeatured;

    public Flower() {
        // Required for Firebase
    }

    public Flower(String id, String name, String categoryId, String category, String description,
                  double price, String imageUrl, int stock, double rating,
                  boolean favorite, int quantity, boolean isPopular, boolean isFeatured) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.category = category;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.rating = rating;
        this.favorite = favorite;
        this.quantity = quantity;
        this.isPopular = isPopular;
        this.isFeatured = isFeatured;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStock() {
        return stock;
    }

    public double getRating() {
        return rating;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isPopular() {
        return isPopular;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }
}