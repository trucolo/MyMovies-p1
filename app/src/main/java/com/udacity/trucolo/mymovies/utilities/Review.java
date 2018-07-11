package com.udacity.trucolo.mymovies.utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable{

    public String author;
    public String content;

    public Review(String author, String content){
        this.author = author;
        this.content = content;
    }

    public Review(Parcel in){
        this.author = in.readString();
        this.content= in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(author);
        dest.writeString(content);
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
