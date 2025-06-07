package com.example.fitsage.data.remote.dto;
import com.example.fitsage.domain.model.Exercise;
import com.example.fitsage.domain.model.Workout;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
public class WorkoutDto {
    @SerializedName("_id")
    private String _id;
    @SerializedName("user_id")
    private String user_id;
    private String date;
    private List<ExerciseDto> exercises;
    private String feedback;

    public String getId() {
        return _id;
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
    public String getUser_id() {
        return user_id;
    }

    public void setId(String _id) {
        this._id = _id;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setExercises(List<ExerciseDto> exercises) {
        this.exercises = exercises;
    }
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Workout toDomain() {
        List<Exercise> domainExercises = new ArrayList<>();
        if (exercises != null) {
            for (ExerciseDto eDto : exercises) {
                domainExercises.add(eDto.toDomain());
            }
        }
        return new Workout(
                _id,
                user_id,
                date,
                domainExercises,
                feedback
        );
    }
    public static WorkoutDto fromDomain(Workout workout) {
        WorkoutDto dto = new WorkoutDto();
        dto.setId(workout.getId());
        dto.setDate(workout.getDate());
        dto.setFeedback(workout.getFeedback());
        dto.setUser_id(workout.getUserId());

        List<ExerciseDto> exerciseDtos = new ArrayList<>();
        if (workout.getExercises() != null) {
            for (Exercise ex : workout.getExercises()) {
                exerciseDtos.add(ExerciseDto.fromDomain(ex));
            }
        }
        dto.setExercises(exerciseDtos);
        return dto;
    }
}
