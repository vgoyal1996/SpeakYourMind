package com.example.vipul.speakyourmind;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MessageKeyModel implements Parcelable,Serializable {
    private String messageKey;
    private String creationDateAndTime;

    public MessageKeyModel(String messageKey, String creationDateAndTime) {
        this.messageKey = messageKey;
        this.creationDateAndTime = creationDateAndTime;
    }

    public MessageKeyModel(Parcel in){
        String[] data = new String[2];
        in.readStringArray(data);
        this.messageKey = data[0];
        this.creationDateAndTime = data[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.messageKey,this.creationDateAndTime});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public MessageKeyModel[] newArray(int size){
            return new MessageKeyModel[size];
        }

        public MessageKeyModel createFromParcel(Parcel in){
            return new MessageKeyModel(in);
        }

    };

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getCreationDateAndTime() {
        return creationDateAndTime;
    }

    public void setCreationDateAndTime(String creationDateAndTime) {
        this.creationDateAndTime = creationDateAndTime;
    }
}
