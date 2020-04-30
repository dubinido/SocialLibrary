package com.example.sociallibrary;

import java.util.List;

public class ChatCon {
    private String user1,user2;
    private List<ChatMessage> chat;

    public ChatCon(String user1, String user2, List<ChatMessage> chat) {
        this.user1 = user1;
        this.user2 = user2;
        this.chat = chat;
    }

    public ChatCon() {
    }

    public String getUser1() {
        return user1;
    }

    public String getUser2() {
        return user2;
    }

    public List<ChatMessage> getChat() {
        return chat;
    }
}
