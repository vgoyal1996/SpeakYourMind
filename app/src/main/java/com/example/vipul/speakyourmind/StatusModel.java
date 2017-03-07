package com.example.vipul.speakyourmind;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class StatusModel implements Parcelable,Serializable {
    private String message;
    private String creationDateAndTime;
    private String uid;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.message,this.creationDateAndTime,this.uid});
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
