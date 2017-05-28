package com.example.vipul.speakyourmind.model;


import java.io.Serializable;
import java.util.List;

public class UserModel implements Serializable {
    private String uid;
    private String userName;
    private String email;
    private String password;
    private String phone;
    private List<StatusModel> statusModelList;
    private List<MessageKeyModel> messageKeyModelList;

    public UserModel(String uid,String userName, String email, String password, String phone) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public UserModel(String userName, String email, String password, String phone) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<StatusModel> getStatusModelList() {
        return statusModelList;
    }

    public void setStatusModelList(List<StatusModel> statusModelList) {
        this.statusModelList = statusModelList;
    }

    public List<MessageKeyModel> getMessageKeyModelList() {
        return messageKeyModelList;
    }

    public void setMessageKeyModelList(List<MessageKeyModel> messageKeyModelList) {
        this.messageKeyModelList = messageKeyModelList;
    }
}
