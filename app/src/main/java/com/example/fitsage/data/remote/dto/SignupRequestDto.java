package com.example.fitsage.data.remote.dto;
public class SignupRequestDto {
    private String email;
    private String username;
    private String password;
    public SignupRequestDto(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
