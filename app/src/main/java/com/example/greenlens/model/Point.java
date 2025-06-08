// model/Point.java
package com.example.greenlens.model;
import java.util.List;

public class Point {
    private String date;
    private String category;
    private int earnedPoints;
    private int totalPoints;

    public Point() {
    }

    public Point(String date, String category, int earnedPoints, int totalPoints) {
        this.date = date;
        this.category = category;
        this.earnedPoints = earnedPoints;
        this.totalPoints = totalPoints;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public int getEarnedPoints() {
        return earnedPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setEarnedPoints(int earnedPoints) {
        this.earnedPoints = earnedPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
}
