package com.example.fitsage.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitsage.R;
import com.example.fitsage.domain.model.Exercise;
import java.util.List;

public class ExerciseDetailAdapter extends RecyclerView.Adapter<ExerciseDetailAdapter.ViewHolder> {

    private final List<Exercise> exercises;

    public ExerciseDetailAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_detail, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ExerciseDetailAdapter.ViewHolder holder, int position) {
        Exercise ex = exercises.get(position);
        holder.nameText.setText(ex.getName());

        String detail;
        if (ex.getSets() != null && ex.getReps() != null) {
            detail = ex.getSets() + " sets × " + ex.getReps() + " reps";
        } else if (ex.getDuration() != null) {
            detail = ex.getDuration();
        } else {
            detail = "—";
        }
        holder.detailText.setText(detail);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, detailText;

        ViewHolder(View itemView) {
            super(itemView);
            nameText   = itemView.findViewById(R.id.textExerciseName);
            detailText = itemView.findViewById(R.id.textExerciseDetail);
        }
    }
}
