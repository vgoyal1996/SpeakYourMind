package com.example.vipul.speakyourmind.model;


import java.io.Serializable;

public class LikeModel implements Serializable {
    private String userUid;
    private boolean isLiked;
    private String likeUid;

    public LikeModel(String userUid, boolean isLiked, String likeUid) {
        this.userUid = userUid;
        this.isLiked = isLiked;
        this.likeUid = likeUid;
    }

    public LikeModel(String userUid, boolean isLiked) {
        this.userUid = userUid;
        this.isLiked = isLiked;
    }

    public String getLikeUid() {
        return likeUid;
    }

    public void setLikeUid(String likeUid) {
        this.likeUid = likeUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
