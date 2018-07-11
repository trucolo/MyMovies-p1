package com.udacity.trucolo.mymovies.utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    public String link;
    public String name;
    public String site;

    public Trailer(String link, String name, String site){
        this.link = link;
        this.name = name;
        this.site = site;
    }

    public Trailer(Parcel in){
        this.link = in.readString();
        this.name= in.readString();
        this.site= in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(link);
        dest.writeString(name);
        dest.writeString(site);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
