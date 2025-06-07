package com.example.fitsage.domain.repository;

import androidx.lifecycle.LiveData;
import com.example.fitsage.domain.model.User;

public interface UserRepository {
    LiveData<String> signup(String username, String email, String password);
    LiveData<User> login(String email, String password);
    LiveData<User> getUser(String userId);
    LiveData<Boolean> updateUserProfile(String userId, User user);
}
