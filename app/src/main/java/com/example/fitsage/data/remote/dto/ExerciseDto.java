package com.example.fitsage.data.remote.dto;

import com.example.fitsage.domain.model.Exercise;
import com.google.gson.annotations.SerializedName;
public class ExerciseDto {
    @SerializedName("name")
    private String name;
    @SerializedName("sets")
    private Integer sets;
    @SerializedName("reps")
    private Integer reps;
    @SerializedName("duration")
    private String duration;
    public ExerciseDto() { }
    public static ExerciseDto fromDomain(Exercise ex) {
        ExerciseDto dto = new ExerciseDto();
        dto.name = ex.getName();
        dto.sets = ex.getSets();
        dto.reps = ex.getReps();
        dto.duration = ex.getDuration();
        return dto;
    }
    public Exercise toDomain() {
        if (sets != null && reps != null) {
            return new Exercise(name, sets, reps);
        } else if (duration != null) {
            return new Exercise(name, duration);
        } else {
            return new Exercise(name, 0, 0);
        }
    }
    public String getName() {
        return name;
    }
    public Integer getSets() {
        return sets;
    }
    public Integer getReps() {
        return reps;
    }
    public String getDuration() {
        return duration;
    }
}
