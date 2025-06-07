package com.example.fitsage.data.remote.dto;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class ChatRequestDto {
    @SerializedName("message")
    private String message;
    @SerializedName("history")
    private List<MessageDto> history;
    public ChatRequestDto(String message, List<MessageDto> history) {
        this.message = message;
        this.history = history;
    }
    public String getMessage() {
        return message;
    }
    public List<MessageDto> getHistory() {
        return history;
    }
    public static class MessageDto {
        @SerializedName("role")
        private String role;
        @SerializedName("content")
        private String content;
        public MessageDto(String role, String content) {
            this.role = role;
            this.content = content;
        }
        public String getRole() { return role;}
        public String getContent() {return content;}
    }
}
