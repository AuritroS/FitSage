package com.example.fitsage.ui.viewmodels;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fitsage.data.remote.api.RetrofitClient;
import com.example.fitsage.data.repository.WorkoutRepositoryImpl;
import com.example.fitsage.domain.model.ChatMessage;
import com.example.fitsage.domain.model.Workout;

import java.util.ArrayList;
import java.util.List;

public class WorkoutViewModel extends ViewModel {

    private final WorkoutRepositoryImpl workoutRepo;

    private final MutableLiveData<List<Workout>> workoutHistory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> addWorkoutResult = new MutableLiveData<>();
    private final MutableLiveData<Workout> generatedWorkout = new MutableLiveData<>();
    private final MutableLiveData<String> logWorkoutResult = new MutableLiveData<>();
    private final MutableLiveData<String> chatResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> testDbResult = new MutableLiveData<>();

    private final MutableLiveData<List<ChatMessage>> chatHistory = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> isGenerating = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public WorkoutViewModel() {
        this.workoutRepo = new WorkoutRepositoryImpl(RetrofitClient.getApiService());
    }

    public LiveData<List<Workout>> getWorkoutHistory() {
        return workoutHistory;
    }

    public LiveData<Boolean> getAddWorkoutResult() {
        return addWorkoutResult;
    }

    public LiveData<Workout> getGeneratedWorkout() {
        return generatedWorkout;
    }

    public LiveData<String> getLogWorkoutResult() {
        return logWorkoutResult;
    }

    public LiveData<String> getChatResult() {
        return chatResult;
    }

    public LiveData<Boolean> getTestDbResult() {
        return testDbResult;
    }

    public LiveData<List<ChatMessage>> getChatHistory() {
        return chatHistory;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsGenerating() {
        return isGenerating;
    }


    public void fetchWorkoutHistory(String userId) {
        workoutRepo.getWorkoutHistory(userId).observeForever(list -> {
            workoutHistory.postValue(list);
        });
    }

    @MainThread
    public void generateWorkout(String userId, int duration, String focus) {
        isGenerating.setValue(true);

        workoutRepo.generateWorkout(userId, duration, focus)
                .observeForever(workout -> {
                    generatedWorkout.postValue(workout);
                    isGenerating.postValue(false);
                });
    }


    public void logWorkout(String userId, Workout workout) {
        workoutRepo.logWorkout(userId, workout).observeForever(logWorkoutResult::postValue);
    }

    @MainThread
    public void sendUserMessage(String userId, String message) {
        List<ChatMessage> currentList = chatHistory.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        currentList.add(new ChatMessage("user", message));
        chatHistory.setValue(new ArrayList<>(currentList));

        isLoading.setValue(true);

        List<ChatMessage> historyCopy = new ArrayList<>(currentList);
        workoutRepo.chat(userId, message, historyCopy).observeForever(aiReply -> {
            List<ChatMessage> updatedList = chatHistory.getValue();
            if (updatedList == null) {
                updatedList = new ArrayList<>();
            }
            updatedList.add(new ChatMessage("ai", aiReply));
            chatHistory.postValue(new ArrayList<>(updatedList));

            isLoading.postValue(false);
        });
    }

    @MainThread
    public void clearChatHistory() {
        chatHistory.setValue(new ArrayList<>());
    }

    public void chat(String userId, String message) {
        sendUserMessage(userId, message);
    }
}