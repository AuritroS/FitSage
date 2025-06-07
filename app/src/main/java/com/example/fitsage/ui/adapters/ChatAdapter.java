package com.example.fitsage.ui.adapters;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitsage.R;
import com.example.fitsage.domain.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        holder.messageTextView.setText(message.getMessage());

        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) holder.messageTextView.getLayoutParams();
        if ("user".equals(message.getSender())) {
            holder.messageContainer.setGravity(Gravity.END);
            holder.messageTextView.setBackgroundResource(R.drawable.chat_bubble_user);
            holder.messageTextView.setTextColor(Color.WHITE);
        } else {
            holder.messageContainer.setGravity(Gravity.START);
            holder.messageTextView.setBackgroundResource(R.drawable.chat_bubble_ai);
            holder.messageTextView.setTextColor(Color.BLACK);
        }
        holder.messageTextView.setLayoutParams(params);

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void updateMessages(List<ChatMessage> newList) {
        messageList.clear();
        messageList.addAll(newList);
        notifyDataSetChanged();
    }


    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        LinearLayout messageContainer;

        public ChatViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageContainer = itemView.findViewById(R.id.messageContainer);
        }
    }
}
