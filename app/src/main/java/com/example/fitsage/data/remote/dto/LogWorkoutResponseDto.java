package com.example.fitsage.data.remote.dto;
public class LogWorkoutResponseDto {
    private String message;
    private String workout_id;
    public String getMessage() {
        return message;
    }
    public String getWorkout_id() {
        return workout_id;
    }
    public String toDomain() {
        return workout_id;
    }
}
