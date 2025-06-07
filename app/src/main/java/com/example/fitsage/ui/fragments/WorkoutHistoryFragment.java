// WorkoutHistoryFragment.java
package com.example.fitsage.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitsage.R;
import com.example.fitsage.domain.model.Workout;
import com.example.fitsage.ui.activities.ProfileSetupActivity;
import com.example.fitsage.ui.adapters.WorkoutHistoryAdapter;
import com.example.fitsage.ui.viewmodels.WorkoutViewModel;

import java.util.List;

public class WorkoutHistoryFragment extends Fragment {
    private static final String TAG = "WorkoutHistoryFrag";
    private static final String ARG_USER_ID = "user_id";

    private ImageButton btnSettings;
    private ProgressBar loadingProgressBar;
    private TextView emptyTextView;
    private RecyclerView historyRecyclerView;

    private WorkoutHistoryAdapter adapter;
    private WorkoutViewModel workoutViewModel;
    private String userId;

    public static WorkoutHistoryFragment newInstance(String userId) {
        WorkoutHistoryFragment fragment = new WorkoutHistoryFragment();
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
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_workout_history, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }

        btnSettings         = view.findViewById(R.id.btnSettings);
        loadingProgressBar  = view.findViewById(R.id.loadingProgressBar);
        emptyTextView       = view.findViewById(R.id.emptyTextView);
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);

        if (btnSettings == null) {
            Log.e(TAG, "btnSettings is null! Check your XML ID.");
        } else {
            Log.d(TAG, "btnSettings found successfully.");
        }

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProfileSetupActivity.class);
            intent.putExtra("heading", "Update Profile");
            intent.putExtra("user_id", userId);
            intent.putExtra("show_logout", true);
            startActivity(intent);
        });

        adapter = new WorkoutHistoryAdapter();
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyRecyclerView.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
        );
        historyRecyclerView.addItemDecoration(divider);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);

        workoutViewModel.getWorkoutHistory().observe(getViewLifecycleOwner(), this::onWorkoutsReceived);

        if (userId == null || userId.isEmpty()) {
            Log.d(TAG, "No userId; skipping fetch");
            showLoading(false);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            showLoading(true);
            workoutViewModel.fetchWorkoutHistory(userId);
        }
    }

    private void onWorkoutsReceived(@Nullable List<Workout> workouts) {
        showLoading(false);

        if (workouts == null || workouts.isEmpty()) {
            adapter.submitList(List.of());
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            adapter.submitList(workouts);
        }
    }

    private void showLoading(boolean isLoading) {
        loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        historyRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        if (isLoading) {
            emptyTextView.setVisibility(View.GONE);
        }
    }
}
