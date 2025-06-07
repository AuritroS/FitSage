package com.example.fitsage.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitsage.R;
import com.example.fitsage.ui.viewmodels.UserViewModel;

public class SignupActivity extends AppCompatActivity {
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton;
    private TextView loginRedirect;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        loginRedirect = findViewById(R.id.loginRedirect);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getSignupResult().observe(this, userId -> {
            if (userId != null) {
                Intent intent = new Intent(SignupActivity.this, ProfileSetupActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignupActivity.this, "Signup failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });

        signupButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            userViewModel.signup(username, email, password);
        });

        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
}
