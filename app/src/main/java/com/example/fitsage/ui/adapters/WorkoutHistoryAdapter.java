package com.example.fitsage.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitsage.R;
import com.example.fitsage.domain.model.Exercise;
import com.example.fitsage.domain.model.Workout;

import java.util.List;
import java.util.stream.Collectors;
public class WorkoutHistoryAdapter extends ListAdapter<Workout, WorkoutHistoryAdapter.ViewHolder> {

    public WorkoutHistoryAdapter() {
        super(WORKOUT_DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public WorkoutHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutHistoryAdapter.ViewHolder holder, int position) {
        Workout workout = getItem(position);
        holder.bind(workout);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;
        private final TextView summaryText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText    = itemView.findViewById(R.id.textWorkoutDate);
            summaryText = itemView.findViewById(R.id.textExerciseSummary);
        }

        public void bind(Workout workout) {
            dateText.setText(workout.getDate());

            List<Exercise> exercises = workout.getExercises();
            if (exercises == null || exercises.isEmpty()) {
                summaryText.setText("No exercises recorded");
            } else {
                String summary = exercises.stream()
                        .map(ex -> {
                            if (ex.getSets() != null && ex.getReps() != null) {
                                return ex.getName() + " (" + ex.getSets() + "×" + ex.getReps() + ")";
                            } else if (ex.getDuration() != null) {
                                return ex.getName() + " (" + ex.getDuration() + ")";
                            } else {
                                return ex.getName();
                            }
                        })
                        .collect(Collectors.joining(", "));
                summaryText.setText(summary);
            }
        }
    }

    private static final DiffUtil.ItemCallback<Workout> WORKOUT_DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Workout>() {
                @Override
                public boolean areItemsTheSame(@NonNull Workout oldItem, @NonNull Workout newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Workout oldItem, @NonNull Workout newItem) {
                    if (!oldItem.getDate().equals(newItem.getDate())) return false;
                    if (oldItem.getExercises().size() != newItem.getExercises().size()) return false;
                    for (int i = 0; i < oldItem.getExercises().size(); i++) {
                        Exercise e1 = oldItem.getExercises().get(i);
                        Exercise e2 = newItem.getExercises().get(i);
                        if (!e1.getName().equals(e2.getName())) return false;
                        if (e1.getSets() == null ^ e2.getSets() == null) return false;
                        if (e1.getReps() == null ^ e2.getReps() == null) return false;
                        if (e1.getDuration() == null ^ e2.getDuration() == null) return false;
                        if (e1.getSets() != null && !e1.getSets().equals(e2.getSets())) return false;
                        if (e1.getReps() != null && !e1.getReps().equals(e2.getReps())) return false;
                        if (e1.getDuration() != null && !e1.getDuration().equals(e2.getDuration())) return false;
                    }
                    return true;
                }
            };
}
