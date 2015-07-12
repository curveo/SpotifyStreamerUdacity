package com.iprodev.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageCustom implements Parcelable {
    public int width;
    public int height;
    public String url;

    public ImageCustom() {
        /* Default Constructor */
    }

    private ImageCustom(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        url = in.readString();
    }

    public static final Parcelable.Creator<ImageCustom> CREATOR = new Parcelable.Creator<ImageCustom>() {

        public ImageCustom createFromParcel(Parcel in) {
            return new ImageCustom(in);
        }

        public ImageCustom[] newArray(int size) {
            return new ImageCustom[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(url);

    }
}
