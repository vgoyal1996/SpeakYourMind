package com.example.vipul.speakyourmind.model;

/**
 * Created by VIPUL on 9/28/2017.
 */

public class ChatModel {
    private String senderId;
    private String receiverId;
    private String message;
    private String timestamp;

    public ChatModel(String senderId, String recipientId, String message, String timestamp) {
        this.senderId = senderId;
        this.receiverId = recipientId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String recipientId) {
        this.receiverId = recipientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ChatModel){
            ChatModel m = (ChatModel)obj;
            if(receiverId.equals(m.getReceiverId())&&senderId.equals(m.getSenderId())&&message.equals(m.getMessage())&&timestamp.equals(m.getTimestamp()))
                return true;
            else if(receiverId.equals(m.getSenderId())&&senderId.equals(m.getReceiverId())&&message.equals(m.getMessage())&&timestamp.equals(m.getTimestamp()))
                return true;
            else
                return false;
        }
        return false;
    }
}
