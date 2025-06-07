package com.example.fitsage.data.remote.dto;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class WorkoutHistoryResponseDto {
    @SerializedName("workouts")
    private List<WorkoutDto> workouts;
    public List<WorkoutDto> getWorkouts() {
        return workouts;
    }
}
