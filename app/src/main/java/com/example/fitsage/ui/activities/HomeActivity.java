
package com.example.fitsage.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitsage.R;
import com.example.fitsage.domain.model.Workout;
import com.example.fitsage.ui.fragments.ChatFragment;
import com.example.fitsage.ui.fragments.WorkoutDetailFragment;
import com.example.fitsage.ui.fragments.WorkoutGeneratorFragment;
import com.example.fitsage.ui.fragments.WorkoutHistoryFragment;
import com.example.fitsage.ui.viewmodels.WorkoutViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Collections;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private String userId;
    private WorkoutViewModel workoutViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userId = getIntent().getStringExtra("user_id");

        workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);

        bottomNav = findViewById(R.id.bottomNav);

        if (savedInstanceState == null) {
            ChatFragment chatFrag = ChatFragment.newInstance(userId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, chatFrag)
                    .commit();
            bottomNav.setSelectedItemId(R.id.menu_chat);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();

            if (id == R.id.menu_chat) {
                selected = ChatFragment.newInstance(userId);
            } else if (id == R.id.menu_generator) {
                selected = WorkoutGeneratorFragment.newInstance(userId);
            } else if (id == R.id.menu_history) {
                selected = WorkoutHistoryFragment.newInstance(userId);
            } else if (id == R.id.menu_detail) {
                selected = WorkoutDetailFragment.newInstance(userId);
            }

            if (selected != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, selected)
                        .commit();
                return true;
            }
            return false;
        });
    }



    @Override
    public void onBackPressed() {
        int selected = bottomNav.getSelectedItemId();
        if (selected != R.id.menu_chat) {
            bottomNav.setSelectedItemId(R.id.menu_chat);
        } else {
            super.onBackPressed();
        }
    }
}
