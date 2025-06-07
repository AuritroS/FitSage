package com.example.fitsage.data.repository;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.fitsage.data.remote.api.ApiService;
import com.example.fitsage.data.remote.dto.ChatRequestDto;
import com.example.fitsage.data.remote.dto.ChatRequestDto.MessageDto;
import com.example.fitsage.data.remote.dto.ChatResponseDto;
import com.example.fitsage.data.remote.dto.ExerciseDto;
import com.example.fitsage.data.remote.dto.GenerateWorkoutRequestDto;
import com.example.fitsage.data.remote.dto.GenerateWorkoutResponseDto;
import com.example.fitsage.data.remote.dto.LogWorkoutRequestDto;
import com.example.fitsage.data.remote.dto.LogWorkoutResponseDto;
import com.example.fitsage.data.remote.dto.WorkoutDto;
import com.example.fitsage.data.remote.dto.WorkoutHistoryResponseDto;
import com.example.fitsage.domain.model.ChatMessage;
import com.example.fitsage.domain.model.Exercise;
import com.example.fitsage.domain.model.Workout;
import com.example.fitsage.domain.repository.WorkoutRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class WorkoutRepositoryImpl implements WorkoutRepository {
    private static final String TAG = "WorkoutRepo";
    private final ApiService apiService;
    public WorkoutRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }
    @Override
    public LiveData<List<Workout>> getWorkoutHistory(String userId) {
        MutableLiveData<List<Workout>> liveData = new MutableLiveData<>();

        Log.d(TAG, "getWorkoutHistory() → calling API with userId=" + userId);
        apiService.getWorkoutHistory(userId).enqueue(new Callback<WorkoutHistoryResponseDto>() {
            @Override
            public void onResponse(Call<WorkoutHistoryResponseDto> call,
                                   Response<WorkoutHistoryResponseDto> response) {
                Log.d(TAG, "getWorkoutHistory onResponse: HTTP " + response.code());

                if (response.isSuccessful()) {
                    WorkoutHistoryResponseDto wrapper = response.body();
                    if (wrapper != null && wrapper.getWorkouts() != null) {
                        List<WorkoutDto> dtoList = wrapper.getWorkouts();
                        Log.d(TAG, "Response body size = " + dtoList.size());

                        List<Workout> domainList = new ArrayList<>();
                        for (WorkoutDto dto : dtoList) {
                            domainList.add(dto.toDomain());
                        }
                        liveData.postValue(domainList);
                    } else {
                        Log.e(TAG, "getWorkoutHistory: response.body() or wrapper.getWorkouts() is null");
                        liveData.postValue(null);
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody()", e);
                    }
                    Log.e(TAG, "getWorkoutHistory: HTTP " + response.code()
                            + " – \"" + errorBody + "\"");
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<WorkoutHistoryResponseDto> call, Throwable t) {
                Log.e(TAG, "getWorkoutHistory onFailure: " + t.getMessage(), t);
                liveData.postValue(null);
            }
        });

        return liveData;
    }
    public LiveData<Workout> generateWorkout(String userId, int duration, String focus) {
        MutableLiveData<Workout> liveData = new MutableLiveData<>();
        GenerateWorkoutRequestDto requestDto = new GenerateWorkoutRequestDto(duration, focus);

        Log.d(TAG, "generateWorkout() → calling API with userId=" + userId
                + ", duration=" + duration + ", focus=\"" + focus + "\"");

        apiService.generateWorkout(userId, requestDto).enqueue(new Callback<GenerateWorkoutResponseDto>() {
            @Override
            public void onResponse(Call<GenerateWorkoutResponseDto> call, Response<GenerateWorkoutResponseDto> response) {
                Log.d(TAG, "generateWorkout onResponse: HTTP " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response body non-null, exercises count = "
                            + (response.body().getWorkout() == null ? "null" : response.body().getWorkout().size()));
                    liveData.postValue(response.body().toDomain(userId));
                } else {
                    try {
                        String errorBody = response.errorBody() == null
                                ? "no errorBody()" : response.errorBody().string();
                        Log.e(TAG, "Unsuccessful response: HTTP " + response.code()
                                + " – \"" + errorBody + "\"");
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody()", e);
                    }
                    liveData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<GenerateWorkoutResponseDto> call, Throwable t) {
                Log.e(TAG, "generateWorkout onFailure: " + t.getMessage(), t);
                liveData.postValue(null);
            }
        });

        return liveData;
    }
    public LiveData<String> logWorkout(String userId, Workout workout) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        LogWorkoutRequestDto requestDTO = new LogWorkoutRequestDto(
                workout.getDate(),
                mapExercisesToDtoList(workout.getExercises()),
                workout.getFeedback()
        );

        apiService.logWorkout(userId, requestDTO).enqueue(new Callback<LogWorkoutResponseDto>() {
            @Override
            public void onResponse(Call<LogWorkoutResponseDto> call, Response<LogWorkoutResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().toDomain());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<LogWorkoutResponseDto> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }
    @Override
    public LiveData<String> chat(String userId, String message, List<ChatMessage> history) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        List<MessageDto> historyDtos = new ArrayList<>();
        if (history != null) {
            // correct:
            for (ChatMessage cm : history) {
                historyDtos.add(new MessageDto(cm.getSender(), cm.getMessage()));
            }

        }
        ChatRequestDto requestDto = new ChatRequestDto(message, historyDtos);
        Log.d(TAG, "chat() → calling API with userId=" + userId + ", message=\"" + message + "\"");
        apiService.chat(userId, requestDto).enqueue(new Callback<ChatResponseDto>() {
            @Override
            public void onResponse(Call<ChatResponseDto> call, Response<ChatResponseDto> response) {
                Log.d(TAG, "chat onResponse: HTTP " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "chat response non-null, reply = \"" + response.body().toDomain() + "\"");
                    liveData.postValue(response.body().toDomain());
                } else {
                    try {
                        String errorBody = response.errorBody() == null
                                ? "no errorBody()" : response.errorBody().string();
                        Log.e(TAG, "chat unsuccessful: HTTP " + response.code()
                                + " – \"" + errorBody + "\"");
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody()", e);
                    }
                    liveData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<ChatResponseDto> call, Throwable t) {
                Log.e(TAG, "chat onFailure: " + t.getMessage(), t);
                liveData.postValue(null);
            }
        });

        return liveData;
    }
    private List<ExerciseDto> mapExercisesToDtoList(List<Exercise> exercises) {
        List<ExerciseDto> dtoList = new ArrayList<>();
        if (exercises != null) {
            for (Exercise ex : exercises) {
                dtoList.add(ExerciseDto.fromDomain(ex));
            }
        }
        return dtoList;
    }
}
