package com.example.fitsage.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fitsage.data.remote.api.RetrofitClient;
import com.example.fitsage.data.repository.UserRepositoryImpl;
import com.example.fitsage.domain.model.User;

public class UserViewModel extends ViewModel {

    private final UserRepositoryImpl userRepo;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> signupResult = new MutableLiveData<>();
    private final MutableLiveData<User> loginResult = new MutableLiveData<>();
    private final MutableLiveData<User> userProfile = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateProfileResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showLogoutFlag = new MutableLiveData<>(false);

    public UserViewModel() {
        this.userRepo = new UserRepositoryImpl(RetrofitClient.getApiService());
    }
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }
    public LiveData<Boolean> getShowLogoutFlag() {
        return showLogoutFlag;
    }

    public void setShowLogoutFlag(boolean show) {
        showLogoutFlag.setValue(show);
    }
    public LiveData<String> getSignupResult() {
        return signupResult;
    }

    public LiveData<User> getLoginResult() {
        return loginResult;
    }

    public LiveData<User> getUserProfile() {
        return userProfile;
    }

    public LiveData<Boolean> getUpdateProfileResult() {
        return updateProfileResult;
    }
    public void signup(String username, String email, String password) {
        userRepo.signup(username, email, password).observeForever(result -> {
            signupResult.postValue(result);
        });
    }

    public void login(String email, String password) {
        userRepo.login(email, password).observeForever(user -> {
            loginResult.postValue(user);
        });
    }

    public void fetchUserProfile(String userId) {
        userRepo.getUser(userId).observeForever(user -> {
            userProfile.postValue(user);
        });
    }
    public void updateProfile(String userId, User updatedUser) {
        userRepo.updateUserProfile(userId, updatedUser).observeForever(success -> {
            updateProfileResult.postValue(success);
        });
    }
}
