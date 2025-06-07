package com.example.fitsage.data.remote.dto;
import java.util.List;
public class LogWorkoutRequestDto {
    private String date;
    private List<ExerciseDto> exercises;
    private String feedback;

    public LogWorkoutRequestDto(String date, List<ExerciseDto> exercises, String feedback) {
        this.date = date;
        this.exercises = exercises;
        this.feedback = feedback;
    }
    public String getDate() {
        return date;
    }
    public List<ExerciseDto> getExercises() {
        return exercises;
    }
    public String getFeedback() {
        return feedback;
    }
}
