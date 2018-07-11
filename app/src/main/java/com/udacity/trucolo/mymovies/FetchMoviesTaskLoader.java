package com.udacity.trucolo.mymovies;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.view.View;

import com.udacity.trucolo.mymovies.utilities.Movie;
import com.udacity.trucolo.mymovies.utilities.NetworkUtils;
import com.udacity.trucolo.mymovies.utilities.TheMoviesDBJsonUtils;

import java.net.URL;
import java.util.ArrayList;

public class FetchMoviesTaskLoader extends AsyncTaskLoader<ArrayList<Movie>> {
    private String mQueryString;
    int pageNumber;
    String apiKey;

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<Movie> loadInBackground() {


        String sortCriteria = mQueryString;
        String page = String.valueOf(pageNumber);
        URL moviesRequestUrl = NetworkUtils.buildUrl(sortCriteria, page, apiKey, getContext());

        try {
            String jsonMoviesResponse = NetworkUtils
                    .getResponseFromHttpUrl(moviesRequestUrl);

            return TheMoviesDBJsonUtils
                    .getMoviesFromJson(getContext(), jsonMoviesResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FetchMoviesTaskLoader(MainActivity context, String queryString, int pageNumber, String apiKey) {
        super(context);
        mQueryString = queryString;
        this.pageNumber = pageNumber;
        this.apiKey = apiKey;
    }
}
