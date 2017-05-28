package com.example.vipul.speakyourmind.model;


import java.io.Serializable;

public class CommentModel implements Serializable {
    private String userUid;
    private String userName;
    private String dateOfComment;
    private String comment;

    public CommentModel(String userName, String dateOfComment, String comment) {
        this.userName = userName;
        this.dateOfComment = dateOfComment;
        this.comment = comment;
    }

    public CommentModel(String userName, String dateOfComment, String comment,String userUid) {
        this.userName = userName;
        this.dateOfComment = dateOfComment;
        this.comment = comment;
        this.userUid = userUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDateOfComment() {
        return dateOfComment;
    }

    public void setDateOfComment(String dateofComment) {
        this.dateOfComment = dateofComment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
