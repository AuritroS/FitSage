// ProfileSetupActivity.java
package com.example.fitsage.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitsage.R;
import com.example.fitsage.domain.model.User;
import com.example.fitsage.ui.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileSetupActivity extends AppCompatActivity {
    private Spinner fitnessLevelSpinner, goalSpinner;
    private CheckBox bodyweightCheckbox, dumbbellsCheckbox, resistanceBandsCheckbox, barbellCheckbox;
    private Button saveProfileButton, logoutButton;
    private UserViewModel userViewModel;
    private String userId;
    private boolean isUpdateMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        userId = getIntent().getStringExtra("user_id");
        String heading = getIntent().getStringExtra("heading");
        isUpdateMode = "Update Profile".equals(heading);

        fitnessLevelSpinner     = findViewById(R.id.fitnessLevelSpinner);
        goalSpinner             = findViewById(R.id.goalSpinner);
        bodyweightCheckbox      = findViewById(R.id.bodyweightCheckbox);
        dumbbellsCheckbox       = findViewById(R.id.dumbbellsCheckbox);
        resistanceBandsCheckbox = findViewById(R.id.resistanceBandsCheckbox);
        barbellCheckbox         = findViewById(R.id.barbellCheckbox);
        saveProfileButton       = findViewById(R.id.saveProfileButton);
        logoutButton            = findViewById(R.id.logoutButton);

        boolean showLogout = getIntent().getBooleanExtra("show_logout", false);
        logoutButton.setVisibility(showLogout ? View.VISIBLE : View.GONE);

        logoutButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(ProfileSetupActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        });

        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.fitness_levels,
                android.R.layout.simple_spinner_item
        );
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fitnessLevelSpinner.setAdapter(levelAdapter);

        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.goals,
                android.R.layout.simple_spinner_item
        );
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        if (isUpdateMode) {
            setTitle("Update Profile");
            saveProfileButton.setText("Update Profile");

            userViewModel.fetchUserProfile(userId);
            userViewModel.getUserProfile().observe(this, profile -> {
                if (profile != null) {
                    String level = profile.getFitnessLevel();
                    if (!TextUtils.isEmpty(level)) {
                        @SuppressWarnings("unchecked")
                        ArrayAdapter<CharSequence> adapter1 =
                                (ArrayAdapter<CharSequence>) fitnessLevelSpinner.getAdapter();
                        int pos1 = adapter1.getPosition(level);
                        if (pos1 >= 0) {
                            fitnessLevelSpinner.setSelection(pos1);
                        }
                    }

                    String goal = profile.getGoal();
                    if (!TextUtils.isEmpty(goal)) {
                        @SuppressWarnings("unchecked")
                        ArrayAdapter<CharSequence> adapter2 =
                                (ArrayAdapter<CharSequence>) goalSpinner.getAdapter();
                        int pos2 = adapter2.getPosition(goal);
                        if (pos2 >= 0) {
                            goalSpinner.setSelection(pos2);
                        }
                    }

                    List<String> eqList = profile.getEquipment();
                    if (eqList != null) {
                        bodyweightCheckbox.setChecked(eqList.contains("Bodyweight"));
                        dumbbellsCheckbox.setChecked(eqList.contains("Dumbbells"));
                        resistanceBandsCheckbox.setChecked(eqList.contains("Resistance Bands"));
                        barbellCheckbox.setChecked(eqList.contains("Barbell and Rack"));
                    }
                }
            });
        } else {
            setTitle("Set Up Profile");
            saveProfileButton.setText("Set Up Profile");
        }

        saveProfileButton.setOnClickListener(v -> {
            String fitnessLevel = fitnessLevelSpinner.getSelectedItem().toString();
            String goal         = goalSpinner.getSelectedItem().toString();
            List<String> equipment = new ArrayList<>();
            if (bodyweightCheckbox.isChecked())      equipment.add("Bodyweight");
            if (dumbbellsCheckbox.isChecked())       equipment.add("Dumbbells");
            if (resistanceBandsCheckbox.isChecked()) equipment.add("Resistance Bands");
            if (barbellCheckbox.isChecked())         equipment.add("Barbell and Rack");

            User updatedUser = new User(
                    userId,
                    "",
                    "",
                    fitnessLevel,
                    goal,
                    equipment,
                    null
            );

            userViewModel.updateProfile(userId, updatedUser);
            userViewModel.getUpdateProfileResult().observe(this, success -> {
                if (Boolean.TRUE.equals(success)) {
                    Toast.makeText(
                            this,
                            isUpdateMode ? "Profile updated!" : "Profile set up!",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
