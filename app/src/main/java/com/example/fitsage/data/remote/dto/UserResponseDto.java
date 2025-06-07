package com.example.fitsage.data.remote.dto;
import com.example.fitsage.domain.model.User;
import java.util.ArrayList;
import java.util.List;
public class UserResponseDto {
    private String user_id;
    private String username;
    private String email;
    private ProfileDto profile;
    private List<WorkoutDto> history;

    public String getUserId() {
        return user_id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public ProfileDto getProfile() {
        return profile;
    }
    public List<WorkoutDto> getHistory() {
        return history;
    }
    public User toDomain() {
        List<com.example.fitsage.domain.model.Workout> domainHistory = new ArrayList<>();
        if (history != null) {
            for (WorkoutDto wDto : history) {
                domainHistory.add(wDto.toDomain());
            }
        }
        return new User(
                user_id,
                username,
                email,
                profile != null ? profile.fitness_level : null,
                profile != null ? profile.goal : null,
                profile != null ? profile.equipment : new ArrayList<>(),
                domainHistory
        );
    }
    public static class ProfileDto {
        private String fitness_level;
        private String goal;
        private List<String> equipment;

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
}
