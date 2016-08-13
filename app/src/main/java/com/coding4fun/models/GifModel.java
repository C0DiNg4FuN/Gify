package com.coding4fun.models;

import android.graphics.Bitmap;

/**
 * Created by coding4fun on 13-Aug-16.
 */

public class GifModel {

    private String name, category, link;
    private Bitmap bitmap;

    public GifModel(String name, String category, Bitmap bitmap) {
        this.name = name;
        this.category = category;
        this.link = "http://www.coding4fun.96.lt/gif/"+name+".gif";
        this.bitmap = bitmap;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCategory() {return category;}

    public void setCategory(String category) {this.category = category;}

    public Bitmap getBitmap() {return bitmap;}

    public void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}
}