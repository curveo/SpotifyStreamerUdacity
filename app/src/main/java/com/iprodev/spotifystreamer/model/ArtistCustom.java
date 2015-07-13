package com.iprodev.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistCustom extends Artist implements Parcelable {

    public String name;
    public String id;
    public ArrayList<ImageCustom> images;

    public ArtistCustom() {
        /* Default constructor */
    }

    private ArtistCustom(Parcel in) {
        name = in.readString();
        id = in.readString();
        images = in.readArrayList(ImageCustom.class.getClassLoader());
    }

    public static final Parcelable.Creator<ArtistCustom> CREATOR
            = new Parcelable.Creator<ArtistCustom>() {
        public ArtistCustom createFromParcel(Parcel in) {
            return new ArtistCustom(in);
        }

        public ArtistCustom[] newArray(int size) {
            return new ArtistCustom[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeList(images);
    }
}
