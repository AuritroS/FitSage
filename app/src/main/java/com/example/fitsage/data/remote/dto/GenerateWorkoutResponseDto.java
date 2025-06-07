package com.example.fitsage.data.remote.dto;
import com.example.fitsage.domain.model.Exercise;
import com.example.fitsage.domain.model.Workout;

import java.util.ArrayList;
import java.util.List;
public class GenerateWorkoutResponseDto {
    private List<ExerciseDto> workout;
    public List<ExerciseDto> getWorkout() {
        return workout;
    }
    public Workout toDomain(String userId) {
        List<Exercise> exList = new ArrayList<>();
        if (workout != null) {
            for (ExerciseDto e : workout) {
                exList.add(e.toDomain());
            }
        }
        return new Workout(
                "",
                userId,
                "",
                exList,
                ""
        );
    }
}

