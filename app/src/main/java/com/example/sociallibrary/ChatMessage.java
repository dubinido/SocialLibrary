package com.example.sociallibrary;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private String messageUserId;

    public ChatMessage(String messageText, String messageUser,String messageUserId)
    {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageUserId=messageUserId;
        this.messageTime = new Date().getTime();
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageUserId() {
        return messageUserId;
    }
}