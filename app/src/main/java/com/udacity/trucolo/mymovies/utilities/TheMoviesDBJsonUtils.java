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
        final String POSTER_PATH = context.getString(R.string.poster_path);
        final String RESULTS = context.getString(R.string.results);
        final String OVERVIEW = context.getString(R.string.overiview);
        final String RELEASE_DATE = context.getString(R.string.release_date);
        final String STATUS_CODE = context.getString(R.string.status_code);
        final int INVALID_API_KEY = 7;

        ArrayList<Movie> dataMovies = new ArrayList<Movie>();
        Movie[] moviesData = null;

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
        moviesData = new Movie[moviesArray.length()];

        for(int i = 0; i < moviesArray.length(); i ++){
            JSONObject movie = moviesArray.getJSONObject(i);
            //Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
            String title = movie.getString(TITLE);
            String releaseDate = movie.getString(RELEASE_DATE);
            String posterLink = movie.getString(POSTER_PATH);
            String posterUrl = NetworkUtils.buildImgUrl(posterLink.replace("/","")).toString();
            String average = movie.getString(VOTE_AVERAGE);
            String description = movie.getString(OVERVIEW);
            Movie m = new Movie(title, releaseDate, posterUrl, context.getString(R.string.sort_by_rating) + average, description);
            dataMovies.add(m);

        }

        return dataMovies;
    }
}
