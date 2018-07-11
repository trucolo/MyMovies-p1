package com.udacity.trucolo.mymovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.trucolo.mymovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

    public final static class FavoriteMovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "favorite_movie";
        public static final String TITLE = "title";
        public static final String RATING = "rating";
        public static final String DESCRIPTION = "description";
        public static final String POSTER = "poster";
        public static final String ID = "id";
    }
}
