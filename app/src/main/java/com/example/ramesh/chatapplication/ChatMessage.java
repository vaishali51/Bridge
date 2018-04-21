package com.example.ramesh.chatapplication;


import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {

    private String messageText, messageUser;
    private String messageTime;

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        //Initiaize to current time
        this.messageTime = String.valueOf(new SimpleDateFormat("hh:mm").format(new Date()));
    }

    public ChatMessage(){

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

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}
