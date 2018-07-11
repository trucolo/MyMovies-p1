package com.udacity.trucolo.mymovies;

import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.udacity.trucolo.mymovies.utilities.NetworkUtils;
import com.udacity.trucolo.mymovies.utilities.TheMoviesDBJsonUtils;
import com.udacity.trucolo.mymovies.utilities.Trailer;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FetchTrailersTaskLoader extends AsyncTaskLoader<Map<String, ArrayList>> {
    private String movieId;
    private String apiKey;

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Map<String, ArrayList> loadInBackground() {
        URL moviesRequestUrl = NetworkUtils.buildUrlForTrailer(apiKey, getContext(), movieId);
        URL reviewsRequestUrl = NetworkUtils.buildUrlForReviews(apiKey, getContext(), movieId);

        try {
            String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
            String jsonReviewsResponse = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);
            ArrayList t = TheMoviesDBJsonUtils.getTrailersFromJson(getContext(), jsonMoviesResponse);
            ArrayList r = TheMoviesDBJsonUtils.getReviewsFromJson(getContext(), jsonReviewsResponse);

            Map<String, ArrayList> trailerReviews = new HashMap<String, ArrayList>();
            trailerReviews.put("trailer", t);
            trailerReviews.put("reviews", r);
            return trailerReviews;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FetchTrailersTaskLoader(MovieDetails context, String movieId, String apiKey) {
        super(context);
        this.movieId = movieId;
        this.apiKey = apiKey;
    }
}