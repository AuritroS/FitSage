package com.example.fitsage.domain.repository;

import androidx.lifecycle.LiveData;

import com.example.fitsage.domain.model.ChatMessage;
import com.example.fitsage.domain.model.Workout;

import java.util.List;

public interface WorkoutRepository {
    LiveData<List<Workout>> getWorkoutHistory(String userId);
    LiveData<String> logWorkout(String userId, Workout workout);
    LiveData<String> chat(String userId, String userMessage, List<ChatMessage> history);
}
