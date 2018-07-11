package com.udacity.trucolo.mymovies.utilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class Movie implements Parcelable{

    //Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
    public String posterUrl;
    public String title;
    public String average;
    public String description;
    public byte[] mPoster;
    public String id;

    public Movie(String title, @Nullable String posterUrl, String average, String description, @Nullable byte[] poster, String id){
        this.title = title;
        this.posterUrl = posterUrl;
        this.average = average;
        this.description = description;
        this.mPoster = poster;
        this.id = id;

    }

    public Movie(Parcel in){
        this.title = in.readString();
        this.posterUrl = in.readString();
        this.average = in.readString();
        this.description = in.readString();
        this.mPoster = in.createByteArray();
        this.id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(posterUrl);
        dest.writeString(average);
        dest.writeString(description);
        dest.writeByteArray(mPoster);
        dest.writeString(id);
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
