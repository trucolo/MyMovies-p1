package com.udacity.trucolo.mymovies.utilities;

import java.io.Serializable;

public class Movie implements Serializable {

    //Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
    private String posterUrl;
    private String title;
    private String releaseDate;
    private String average;
    private String description;

    public Movie(String title, String releaseDate, String posterUrl, String average, String description){
        this.title = title;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.average = average;
        this.description = description;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getAverage() {
        return average;
    }

    public String getDescription() {
        return description;
    }

}
