package com.example.fitsage.data.remote.dto;
import java.util.List;
public class ProfileUpdateRequestDto {
    private String fitness_level;
    private String goal;
    private List<String> equipment;
    public ProfileUpdateRequestDto(String fitness_level, String goal, List<String> equipment) {
        this.fitness_level = fitness_level;
        this.goal = goal;
        this.equipment = equipment;
    }
    public String getFitness_level() {
        return fitness_level;
    }
    public String getGoal() {
        return goal;
    }
    public List<String> getEquipment() {
        return equipment;
    }
}
