package com.example.fitsage.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitsage.R;
import com.example.fitsage.domain.model.ChatMessage;
import com.example.fitsage.ui.adapters.ChatAdapter;
import com.example.fitsage.ui.viewmodels.WorkoutViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton , clearButton;
    private ProgressBar loadingSpinner;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatHistory = new ArrayList<>();

    private WorkoutViewModel workoutViewModel;
    private String userId;

    public static ChatFragment newInstance(String userId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getString("user_id");
        }

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput     = view.findViewById(R.id.messageInput);
        sendButton       = view.findViewById(R.id.sendButton);
        clearButton       = view.findViewById(R.id.clearButton);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);

        chatAdapter = new ChatAdapter(chatHistory);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        workoutViewModel = new ViewModelProvider(requireActivity()).get(WorkoutViewModel.class);

        workoutViewModel.getChatHistory().observe(getViewLifecycleOwner(), historyList -> {
            chatAdapter.updateMessages(historyList);
            if (!historyList.isEmpty()) {
                chatRecyclerView.scrollToPosition(historyList.size() - 1);
            }
        });

        workoutViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (Boolean.TRUE.equals(loading)) {
                loadingSpinner.setVisibility(View.VISIBLE);
                sendButton.setEnabled(false);
            } else {
                loadingSpinner.setVisibility(View.GONE);
                sendButton.setEnabled(true);
            }
        });
        clearButton.setOnClickListener(v -> {
            workoutViewModel.clearChatHistory();
        });



        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (TextUtils.isEmpty(msg)) {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }
            workoutViewModel.sendUserMessage(userId, msg);

            messageInput.setText("");
        });

    }
}
