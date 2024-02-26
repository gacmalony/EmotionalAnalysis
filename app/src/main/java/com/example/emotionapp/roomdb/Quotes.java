package com.example.emotionapp.roomdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName="quotes")
public class Quotes {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "quote_id")
    private int id;

    @ColumnInfo(name= "image_url")
    private String image_url;



    public String getImage_url() {
        return image_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public Quotes(String quote, String image_url) {
        this.quote = quote;
        this.image_url = image_url;
    }

    @ColumnInfo(name = "quote_name")
    private String quote;



}
