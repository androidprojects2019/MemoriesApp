package com.example.yourmemories.DataBase.Model;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
@Entity
public class Memory {

    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo
    String title;
    @ColumnInfo
    String description;
    @ColumnInfo
    String dateTime;
    @ColumnInfo
    String image;
    @ColumnInfo
    public double latitude ;
    @ColumnInfo
    public double longitude;

    public double getLat() {
        return latitude ;
    }

    public void setLat(double lat) {
        this.latitude  = lat;
    }

    public double getLong() {
        return longitude;
    }

    public void setLang(double lang) {
        this.longitude = lang;
    }
    @Ignore
    public Memory( String title, String description, String dateTime, String image, double latitude, double longitude) {
         this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.image = image;
        this.latitude  = latitude;
        this.longitude = longitude;
    }

    public Memory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
