// WorkoutGeneratorFragment.java
package com.example.fitsage.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitsage.R;
import com.example.fitsage.domain.model.Exercise;
import com.example.fitsage.domain.model.Workout;
import com.example.fitsage.ui.activities.HomeActivity;
import com.example.fitsage.ui.viewmodels.WorkoutViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class WorkoutGeneratorFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";

    private String userId;
    private WorkoutViewModel workoutViewModel;
    private Button generateWorkoutButton;
    private Button viewWorkoutButton;
    private ProgressBar generationSpinner;
    private EditText durationEditText;
    private EditText focusEditText;
    private TextView workoutTitle;
    private LinearLayout exercisesContainer;

    private Workout currentWorkout;

    public static WorkoutGeneratorFragment newInstance(String userId) {
        WorkoutGeneratorFragment fragment = new WorkoutGeneratorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_generated_workout, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }

        durationEditText      = view.findViewById(R.id.durationEditText);
        focusEditText         = view.findViewById(R.id.focusEditText);
        generateWorkoutButton = view.findViewById(R.id.generateWorkoutButton);
        viewWorkoutButton     = view.findViewById(R.id.viewWorkoutButton);
        workoutTitle          = view.findViewById(R.id.workoutTitle);
        exercisesContainer    = view.findViewById(R.id.exercisesContainer);
        generationSpinner     = view.findViewById(R.id.savingProgressBar);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);

        generationSpinner.setVisibility(View.GONE);
        viewWorkoutButton.setVisibility(View.GONE);

        workoutViewModel.getIsGenerating().observe(getViewLifecycleOwner(), isGen -> {
            if (Boolean.TRUE.equals(isGen)) {
                generationSpinner.setVisibility(View.VISIBLE);
                generateWorkoutButton.setEnabled(false);
                viewWorkoutButton.setVisibility(View.GONE);
            } else {
                generationSpinner.setVisibility(View.GONE);
                generateWorkoutButton.setEnabled(true);
            }
        });

        workoutViewModel.getGeneratedWorkout().observe(getViewLifecycleOwner(), workout -> {
            if (workout != null) {
                currentWorkout = workout;
                displayWorkout(workout);
                viewWorkoutButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(requireContext(), "Failed to generate workout", Toast.LENGTH_SHORT).show();
            }
        });

        generateWorkoutButton.setOnClickListener(v -> {
            String durationText = durationEditText.getText().toString().trim();
            String focusText    = focusEditText.getText().toString().trim();

            if (TextUtils.isEmpty(durationText)) {
                Toast.makeText(requireContext(), "Please enter a duration", Toast.LENGTH_SHORT).show();
                return;
            }

            int duration;
            try {
                duration = Integer.parseInt(durationText);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Duration must be a number", Toast.LENGTH_SHORT).show();
                return;
            }

            workoutViewModel.generateWorkout(userId, duration, focusText);
        });

        viewWorkoutButton.setOnClickListener(v -> {
            WorkoutDetailFragment detailFrag = WorkoutDetailFragment.newInstance(userId);
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, detailFrag)
                    .addToBackStack(null)
                    .commit();

            HomeActivity parent = (HomeActivity) requireActivity();
            BottomNavigationView bottomNav = parent.findViewById(R.id.bottomNav);
            bottomNav.setSelectedItemId(R.id.menu_detail);
        });
    }

    private void displayWorkout(Workout workout) {
        workoutTitle.setText("Your Workout:");
        exercisesContainer.removeAllViews();

        List<Exercise> exercises = workout.getExercises();
        if (exercises == null || exercises.isEmpty()) return;

        for (Exercise ex : exercises) {
            TextView exView = new TextView(requireContext());
            exView.setTextSize(16f);

            String line = "- " + ex.getName();
            if (ex.getReps() != null && ex.getSets() != null) {
                line += ": " + ex.getSets() + " × " + ex.getReps();
            } else if (ex.getDuration() != null) {
                line += ": " + ex.getDuration();
            }
            exView.setText(line);
            exercisesContainer.addView(exView);
        }
    }
}
