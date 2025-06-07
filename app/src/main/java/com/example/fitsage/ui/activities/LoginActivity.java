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
import com.example.fitsage.domain.model.User;
import com.example.fitsage.ui.viewmodels.UserViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signupRedirect;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupRedirect = findViewById(R.id.signupRedirect);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getLoginResult().observe(this, user -> {
            if (user != null) {
                // Login succeeded: pass user_id to HomeActivity
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("user_id", user.getId());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Login failed. Check credentials.", Toast.LENGTH_SHORT).show();
            }
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            userViewModel.login(email, password);
        });

        signupRedirect.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }
}
