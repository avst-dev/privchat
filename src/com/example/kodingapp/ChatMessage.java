package com.example.kodingapp;
public class ChatMessage {
    private String sender, message, avatarBase64; private boolean isMe;
    public ChatMessage(String s, String m, String a, boolean i) { sender=s; message=m; avatarBase64=a; isMe=i; }
    public String getSender() { return sender; }
    public String getMessage() { return message; }
    public String getAvatarBase64() { return avatarBase64; }
    public boolean isMe() { return isMe; }
}
