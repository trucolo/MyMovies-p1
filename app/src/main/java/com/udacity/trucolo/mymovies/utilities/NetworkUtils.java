package com.udacity.trucolo.mymovies.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.udacity.trucolo.mymovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {



    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String DYNAMIC_IMAGES_URL = Resources.getSystem().getString(R.string.images_url);
    private static final String IMAGE_SIZE = Resources.getSystem().getString(R.string.image_size);
    private static final String MOVIES_BASE_URL = Resources.getSystem().getString(R.string.api_url);

    /* The format we want our API to return */
    private static final String format = Resources.getSystem().getString(R.string.json);

    private final static String API_KEY_PARAM = Resources.getSystem().getString(R.string.api_key);

    /*
    Example of API being called:
    https://api.themoviedb.org/3/discover/movie?api_key=<API_KEY>&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=2
    */

    private final static String LANG_PARAM = Resources.getSystem().getString(R.string.language);
    private final static String LANG = Resources.getSystem().getString(R.string.en_us);
    private final static String SORT_PARAM = Resources.getSystem().getString(R.string.sort_by_param);
    private final static String ADULT_PARAM = Resources.getSystem().getString(R.string.include_adult);
    private final static String INCLUDE_VIDEO_PARAM = Resources.getSystem().getString(R.string.include_video);
    private final static String PAGE_PARAM = Resources.getSystem().getString(R.string.page);

    /**
     * Builds the URL used to talk to the moviesDB server using a sort parameter. This location is based
     * on the query capabilities of the moviesDB provider that we are using.
     *
     * @param sortBy The argument that will be sorted for.
     * @return The URL to use to query the moviesDB server.
     */

    public static URL buildUrl(String sortBy, String pageNumber, String apiKey){
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(LANG_PARAM, LANG)
                .appendQueryParameter(SORT_PARAM, sortBy)
                .appendQueryParameter(ADULT_PARAM, Resources.getSystem().getString(R.string.false_flag))
                .appendQueryParameter(INCLUDE_VIDEO_PARAM, Resources.getSystem().getString(R.string.false_flag))
                .appendQueryParameter(PAGE_PARAM, pageNumber)
                .build();

        URL url = null;

        try{
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method is intended to check whether the url is valid
     *
     * @param posterPath
     * @return builtUri to download the image
     *
     * */
    public static Uri buildImgUrl(String posterPath){
        Uri builtUri = Uri.parse(DYNAMIC_IMAGES_URL).buildUpon().appendPath(IMAGE_SIZE).appendPath(posterPath).build();
        return builtUri;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
