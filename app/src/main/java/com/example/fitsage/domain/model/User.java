package com.example.fitsage.domain.model;

import java.util.List;

public class User {
    private String id;
    private String username;
    private String email;
    private String fitnessLevel;
    private String goal;
    private List<String> equipment;
    private List<Workout> history;

    public User(String id, String username, String email, String fitnessLevel, String goal, List<String> equipment, List<Workout> history) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fitnessLevel = fitnessLevel;
        this.goal = goal;
        this.equipment = equipment;
        this.history = history;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFitnessLevel() { return fitnessLevel; }
    public String getGoal() { return goal; }
    public List<String> getEquipment() { return equipment; }
    public List<Workout> getHistory() { return history; }
}
