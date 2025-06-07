package com.example.fitsage.data.repository;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.fitsage.data.remote.api.ApiService;
import com.example.fitsage.data.remote.dto.LoginRequestDto;
import com.example.fitsage.data.remote.dto.ProfileUpdateRequestDto;
import com.example.fitsage.data.remote.dto.SignupRequestDto;
import com.example.fitsage.data.remote.dto.UserResponseDto;
import com.example.fitsage.domain.model.User;
import com.example.fitsage.domain.repository.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class UserRepositoryImpl implements UserRepository {
    private final ApiService apiService;
    public UserRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }
    @Override
    public LiveData<String> signup(String username, String email, String password) {
        MutableLiveData<String> liveData = new MutableLiveData<>();
        SignupRequestDto request = new SignupRequestDto(email, username, password);
        apiService.signup(request).enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().getUserId());
                } else {
                    liveData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }
    @Override
    public LiveData<User> login(String email, String password) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        LoginRequestDto request = new LoginRequestDto(email, password);
        apiService.login(request).enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().toDomain());
                } else {
                    liveData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }
    @Override
    public LiveData<User> getUser(String userId) {
        MutableLiveData<User> liveData = new MutableLiveData<>();
        apiService.getProfile(userId).enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().toDomain());
                } else {
                    liveData.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }
    @Override
    public LiveData<Boolean> updateUserProfile(String userId, User user) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        ProfileUpdateRequestDto request = new ProfileUpdateRequestDto(
                user.getFitnessLevel(),
                user.getGoal(),
                user.getEquipment()
        );
        apiService.saveProfile(userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                liveData.postValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                liveData.postValue(false);
            }
        });
        return liveData;
    }
}
