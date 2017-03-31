package com.example.vipul.speakyourmind;


import java.io.Serializable;

public class CommentModel implements Serializable {
    private String userName;
    private String dateOfComment;
    private String comment;

    public CommentModel(String dateOfComment, String comment) {
        this.dateOfComment = dateOfComment;
        this.comment = comment;
    }

    public CommentModel(String userName, String dateOfComment, String comment) {
        this.userName = userName;
        this.dateOfComment = dateOfComment;
        this.comment = comment;
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
