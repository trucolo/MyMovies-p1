package com.udacity.trucolo.mymovies.utilities;

import android.content.Context;
import android.util.Log;

import com.udacity.trucolo.mymovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class TheMoviesDBJsonUtils {

    private final static String TAG = TheMoviesDBJsonUtils.class.getSimpleName();

    public static ArrayList<Movie> getMoviesFromJson(Context context, String moviesJsonStr) throws JSONException{

        //Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.

        final String VOTE_AVERAGE = context.getString(R.string.vote_average);
        final String TITLE = context.getString(R.string.title);
        final String ID = context.getString(R.string.id);
        final String POSTER_PATH = context.getString(R.string.poster_path);
        final String RESULTS = context.getString(R.string.results);
        final String OVERVIEW = context.getString(R.string.overview);
        final String STATUS_CODE = context.getString(R.string.status_code);
        final int INVALID_API_KEY = 7;

        ArrayList<Movie> dataMovies = new ArrayList<Movie>();

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        if(moviesJson.has(STATUS_CODE)){
            int errorCode = moviesJson.getInt(STATUS_CODE);
            switch (errorCode) {
                case INVALID_API_KEY:
                    Log.e(TAG, "INVALID API KEY");
                    break;
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }

        }

        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        for(int i = 0; i < moviesArray.length(); i ++){
            JSONObject movie = moviesArray.getJSONObject(i);
            //Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.

            String title = movie.getString(TITLE);
            String posterLink = movie.getString(POSTER_PATH);
            String posterUrl = NetworkUtils.buildImgUrl(posterLink.replace("/","")).toString();
            String average = movie.getString(VOTE_AVERAGE);
            String description = movie.getString(OVERVIEW);
            String id = movie.getString(ID);
            Movie m = new Movie(title, posterUrl, average, description, null, id);
            dataMovies.add(m);

        }

        return dataMovies;
    }

    public static ArrayList<Trailer> getTrailersFromJson(Context context, String trailerJsonStr) throws JSONException{

        final String RESULTS = context.getString(R.string.results);
        final String STATUS_CODE = context.getString(R.string.status_code);
        final int INVALID_API_KEY = 7;

        final String KEY = context.getString(R.string.key);
        final String NAME = context.getString(R.string.name);
        final String SITE = context.getString(R.string.site);

        ArrayList<Trailer> trailers = new ArrayList<Trailer>();

        JSONObject moviesJson = new JSONObject(trailerJsonStr);

        if(moviesJson.has(STATUS_CODE)){
            int errorCode = moviesJson.getInt(STATUS_CODE);
            switch (errorCode) {
                case INVALID_API_KEY:
                    Log.e(TAG, "INVALID API KEY");
                    break;
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }

        }

        JSONArray trailersArray = moviesJson.getJSONArray(RESULTS);

        for(int i = 0; i < trailersArray.length(); i ++){
            JSONObject trailer = trailersArray.getJSONObject(i);
            //Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
            String title = trailer.getString(NAME);
            String url = context.getString(R.string.youtube_url) + trailer.getString(KEY);
            String site = trailer.getString(SITE);
            Trailer t = new Trailer(url, title, site);
            trailers.add(t);
        }
        return trailers;
    }

    public static ArrayList<Review> getReviewsFromJson(Context context, String reviewJsonStr) throws JSONException{

        final String RESULTS = context.getString(R.string.results);
        final String STATUS_CODE = context.getString(R.string.status_code);
        final int INVALID_API_KEY = 7;

        final String NAME = context.getString(R.string.author);
        final String CONTENT = context.getString(R.string.content);

        ArrayList<Review> reviews = new ArrayList<Review>();

        JSONObject moviesJson = new JSONObject(reviewJsonStr);

        if(moviesJson.has(STATUS_CODE)){
            int errorCode = moviesJson.getInt(STATUS_CODE);
            switch (errorCode) {
                case INVALID_API_KEY:
                    Log.e(TAG, "INVALID API KEY");
                    break;
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }

        }

        JSONArray trailersArray = moviesJson.getJSONArray(RESULTS);

        for(int i = 0; i < trailersArray.length(); i ++){
            JSONObject trailer = trailersArray.getJSONObject(i);
            //Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
            String author = trailer.getString(NAME);
            String content = trailer.getString(CONTENT);
            Review r = new Review(author, content);
            reviews.add(r);
        }
        return reviews;
    }
}
