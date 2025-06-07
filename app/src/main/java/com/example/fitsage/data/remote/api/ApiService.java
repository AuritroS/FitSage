package com.example.fitsage.data.remote.api;
import com.example.fitsage.data.remote.dto.GenerateWorkoutRequestDto;
import com.example.fitsage.data.remote.dto.LoginRequestDto;
import com.example.fitsage.data.remote.dto.SignupRequestDto;
import com.example.fitsage.data.remote.dto.UserResponseDto;
import com.example.fitsage.data.remote.dto.ProfileUpdateRequestDto;
import com.example.fitsage.data.remote.dto.ChatRequestDto;
import com.example.fitsage.data.remote.dto.ChatResponseDto;
import com.example.fitsage.data.remote.dto.GenerateWorkoutResponseDto;
import com.example.fitsage.data.remote.dto.LogWorkoutRequestDto;
import com.example.fitsage.data.remote.dto.LogWorkoutResponseDto;
import com.example.fitsage.data.remote.dto.WorkoutHistoryResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
public interface ApiService {
    @POST("/signup")
    Call<UserResponseDto> signup(@Body SignupRequestDto request);
    @POST("/login")
    Call<UserResponseDto> login(@Body LoginRequestDto request);
    @POST("/profile/{userId}")
    Call<Void> saveProfile(
            @Path("userId") String userId,
            @Body ProfileUpdateRequestDto profile
    );
    @GET("/profile/{userId}")
    Call<UserResponseDto> getProfile(@Path("userId") String userId);
    @POST("/chat/{userId}")
    Call<ChatResponseDto> chat(
            @Path("userId") String userId,
            @Body ChatRequestDto request
    );
    @POST("/generate_workout/{userId}")
    Call<GenerateWorkoutResponseDto> generateWorkout(@Path("userId") String userId, @Body GenerateWorkoutRequestDto request);
    @POST("/log_workout/{userId}")
    Call<LogWorkoutResponseDto> logWorkout(
            @Path("userId") String userId,
            @Body LogWorkoutRequestDto request
    );
    @GET("/workouts/{userId}")
    Call<WorkoutHistoryResponseDto> getWorkoutHistory(@Path("userId") String userId);
}
