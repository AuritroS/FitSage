package com.example.fitsage.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitsage.R;
import com.example.fitsage.domain.model.Exercise;
import com.example.fitsage.domain.model.Workout;
import com.example.fitsage.ui.activities.HomeActivity;
import com.example.fitsage.ui.adapters.ExerciseDetailAdapter;
import com.example.fitsage.ui.viewmodels.WorkoutViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";

    private String userId;
    private Workout currentWorkout;
    private WorkoutViewModel workoutViewModel;

    private View placeholderContainer;
    private View workoutContainer;
    private TextView dateTextView;
    private RecyclerView exercisesRecyclerView;
    private Button completeWorkoutButton;
    private Button btnGenerateWorkout;

    public static WorkoutDetailFragment newInstance(String userId) {
        WorkoutDetailFragment fragment = new WorkoutDetailFragment();
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
        return inflater.inflate(R.layout.fragment_workout_detail, container, false);
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

        placeholderContainer   = view.findViewById(R.id.placeholderContainer);
        workoutContainer       = view.findViewById(R.id.workoutContainer);
        dateTextView           = view.findViewById(R.id.textWorkoutDate);
        exercisesRecyclerView  = view.findViewById(R.id.recyclerExercises);
        completeWorkoutButton  = view.findViewById(R.id.completeWorkoutButton);
        btnGenerateWorkout     = view.findViewById(R.id.btnGenerateWorkout);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);

        showPlaceholderUI();

        workoutViewModel.getGeneratedWorkout().observe(
                getViewLifecycleOwner(),
                newWorkout -> {
                    if (newWorkout != null && !isEmptyWorkout(newWorkout)) {
                        currentWorkout = newWorkout;
                        showWorkoutUI(newWorkout);
                    }
                }
        );

        completeWorkoutButton.setOnClickListener(v -> {
            if (currentWorkout == null || isEmptyWorkout(currentWorkout)) {
                Toast.makeText(requireContext(), "No workout to log", Toast.LENGTH_SHORT).show();
                return;
            }
            completeWorkoutButton.setEnabled(false);
            workoutViewModel.logWorkout(userId, currentWorkout);

            workoutViewModel.getLogWorkoutResult().observe(
                    getViewLifecycleOwner(),
                    workoutId -> {
                        if (!TextUtils.isEmpty(workoutId)) {
                            Toast.makeText(requireContext(), "Workout logged: " + workoutId, Toast.LENGTH_SHORT).show();
                            // Navigate to “History” in bottom nav
                            HomeActivity parent = (HomeActivity) requireActivity();
                            BottomNavigationView bottomNav = parent.findViewById(R.id.bottomNav);
                            bottomNav.setSelectedItemId(R.id.menu_history);
                        } else {
                            Toast.makeText(requireContext(), "Failed to log workout", Toast.LENGTH_SHORT).show();
                            completeWorkoutButton.setEnabled(true);
                        }
                    }
            );
        });

        btnGenerateWorkout.setOnClickListener(v -> {
            WorkoutGeneratorFragment genFrag = WorkoutGeneratorFragment.newInstance(userId);
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, genFrag)
                    .addToBackStack(null)
                    .commit();

            HomeActivity parent = (HomeActivity) requireActivity();
            BottomNavigationView bottomNav = parent.findViewById(R.id.bottomNav);
            bottomNav.setSelectedItemId(R.id.menu_generator);
        });
    }

    private boolean isEmptyWorkout(Workout workout) {
        return workout == null
                || ((workout.getExercises() == null || workout.getExercises().isEmpty())
                && (workout.getDate() == null || workout.getDate().isEmpty()));
    }

    private void showPlaceholderUI() {
        placeholderContainer.setVisibility(View.VISIBLE);
        workoutContainer.setVisibility(View.GONE);

        dateTextView.setVisibility(View.GONE);
        exercisesRecyclerView.setVisibility(View.GONE);
        completeWorkoutButton.setVisibility(View.GONE);
    }

    private void showWorkoutUI(Workout workout) {
        placeholderContainer.setVisibility(View.GONE);
        workoutContainer.setVisibility(View.VISIBLE);

        String dateOrTitle = (workout.getDate() != null && !workout.getDate().isEmpty())
                ? workout.getDate()
                : "Your Workout:";
        dateTextView.setText(dateOrTitle);

        List<Exercise> exercises = workout.getExercises();
        if (exercises == null) {
            exercises = new ArrayList<>();
        }
        ExerciseDetailAdapter adapter = new ExerciseDetailAdapter(exercises);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        exercisesRecyclerView.setAdapter(adapter);
        exercisesRecyclerView.setVisibility(View.VISIBLE);

        completeWorkoutButton.setVisibility(View.VISIBLE);
        completeWorkoutButton.setEnabled(true);
    }
}
