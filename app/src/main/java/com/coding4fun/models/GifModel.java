package com.coding4fun.models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by coding4fun on 13-Aug-16.
 */

public class GifModel implements Serializable {

    private String name, category, link, size;
    private transient Bitmap bitmap;

    public GifModel(String name, String category, String size, Bitmap bitmap) {
        this.name = name;
        this.category = category;
        this.link = "http://www.coding4fun.96.lt/gif/"+name+".gif";
        this.size = size;
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

    public String getSize() {return size;}

    public void setSize(String size) {this.size = size;}
}