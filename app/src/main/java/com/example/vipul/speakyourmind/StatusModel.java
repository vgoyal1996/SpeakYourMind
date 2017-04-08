package com.example.vipul.speakyourmind;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class StatusModel implements Parcelable,Serializable {
    private String message;
    private String creationDateAndTime;
    private String uid;
    private List<CommentModel> commentList;
    private List<LikeModel> likeList;

    public StatusModel(String message, String creationDateAndTime, List<CommentModel> commentList) {
        this.message = message;
        this.creationDateAndTime = creationDateAndTime;
        this.commentList = commentList;
    }

    public StatusModel(String message, String creationDateAndTime, String uid, List<CommentModel> commentList) {
        this.message = message;
        this.creationDateAndTime = creationDateAndTime;
        this.uid = uid;
        this.commentList = commentList;
    }

    public StatusModel(String message, String creationDateAndTime, List<CommentModel> commentList, List<LikeModel> likeList) {
        this.message = message;
        this.creationDateAndTime = creationDateAndTime;
        this.commentList = commentList;
        this.likeList = likeList;
    }

    public StatusModel(String message, String creationDateAndTime, String uid, List<CommentModel> commentList, List<LikeModel> likeList) {
        this.message = message;
        this.creationDateAndTime = creationDateAndTime;
        this.uid = uid;
        this.commentList = commentList;
        this.likeList = likeList;
    }

    public StatusModel(String message, String creationDateAndTime) {
        this.message = message;
        this.creationDateAndTime = creationDateAndTime;
    }

    public StatusModel(String message, String creationDateAndTime, String uid) {
        this.message = message;
        this.creationDateAndTime = creationDateAndTime;
        this.uid = uid;
    }

    public StatusModel(Parcel in){
        String[] data = new String[3];
        in.readStringArray(data);
        this.message = data[0];
        this.creationDateAndTime = data[1];
        this.uid = data[2];
        List<CommentModel> list = null;
        in.readList(list,CommentModel.class.getClassLoader());
        List<LikeModel> likeModels = null;
        in.readList(likeModels,LikeModel.class.getClassLoader());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreationDateAndTime() {
        return creationDateAndTime;
    }

    public void setCreationDateAndTime(String creationDateAndTime) {
        this.creationDateAndTime = creationDateAndTime;
    }

    public List<CommentModel> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentModel> commentList) {
        this.commentList = commentList;
    }

    public List<LikeModel> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<LikeModel> likeList) {
        this.likeList = likeList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.message,this.creationDateAndTime,this.uid});
        parcel.writeList(commentList);
        parcel.writeList(likeList);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public StatusModel[] newArray(int size){
            return new StatusModel[size];
        }

        public StatusModel createFromParcel(Parcel in){
            return new StatusModel(in);
        }

    };

}
