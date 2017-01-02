package com.cs496.proj2.project2;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by q on 2017-01-03.
 */

public class JoongoEntry implements Serializable {
    public boolean soldOut;
    public Bitmap image;
    public String name;
    public String price;
    public boolean negotiable;
    public boolean delivery;
    public String desc;
}
