package com.example.fitsage.domain.model;
import java.util.ArrayList;
import java.util.List;
public class Workout  {
    private final String id;
    private final String userId;
    private final String date;
    private final List<Exercise> exercises;
    private final String feedback;
    public Workout(String id, String userId, String date, List<Exercise> exercises, String feedback) {
        this.id        = id;
        this.userId    = userId;
        this.date      = date;
        this.exercises = exercises;
        this.feedback  = feedback;
    }
    public String getId() {
        return id;
    }
    public String getUserId() {
        return userId;
    }
    public String getDate() {
        return date;
    }
    public List<Exercise> getExercises() {
        return exercises;
    }
    public String getFeedback() {
        return feedback;
    }
}
