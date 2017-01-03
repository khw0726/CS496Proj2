package com.cs496.proj2.project2;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by q on 2017-01-03.
 */

public class JoongoEntry {
    public String id;
    public boolean soldOut;
    public Uri image;
    public Bitmap thumbnail;
    public String name;
    public String price;
    public boolean negotiable;
    public boolean delivery;
    public String desc;

}
