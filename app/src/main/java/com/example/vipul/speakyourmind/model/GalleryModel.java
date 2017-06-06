package com.example.vipul.speakyourmind.model;


public class GalleryModel {
    private int startingID;
    private int clickedID;
    private int gallerySize;

    public GalleryModel(int startingID, int clickedID, int gallerySize) {
        this.startingID = startingID;
        this.clickedID = clickedID;
        this.gallerySize = gallerySize;
    }

    public int getStartingID() {
        return startingID;
    }

    public void setStartingID(int startingID) {
        this.startingID = startingID;
    }

    public int getClickedID() {
        return clickedID;
    }

    public void setClickedID(int clickedID) {
        this.clickedID = clickedID;
    }

    public int getGallerySize() {
        return gallerySize;
    }

    public void setGallerySize(int gallerySize) {
        this.gallerySize = gallerySize;
    }
}
