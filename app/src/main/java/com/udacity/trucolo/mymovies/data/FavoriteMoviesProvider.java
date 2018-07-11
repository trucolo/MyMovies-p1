package com.udacity.trucolo.mymovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoriteMoviesProvider extends ContentProvider{

    public static final int FAVORITE_MOVIES = 100;
    public static final int CODE_MOVIE_WITH_ID= 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoriteMoviesDbHelper mOpenHelper;




    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id = db.insert(FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME, null, values);
        if(id > 0){
            returnUri = ContentUris.withAppendedId(FavoriteMoviesContract.CONTENT_URI, id);

        }else{
            throw new android.database.SQLException("Failed to insert into database " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;
        String[] selectionArguments = new String[]{uri.getLastPathSegment()};

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE_WITH_ID:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        FavoriteMoviesContract.FavoriteMovieEntry.ID + "=?",
                        selectionArguments);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoriteMoviesContract.PATH_FAVORITES, FAVORITE_MOVIES);

        matcher.addURI(authority, FavoriteMoviesContract.PATH_FAVORITES + "/#", CODE_MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoriteMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;
        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE_WITH_ID: {

                String[] selectionArguments = new String[]{uri.getLastPathSegment()};

                cursor = mOpenHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArguments,
                        null,
                        null,
                        null);

                break;
            }

            case FAVORITE_MOVIES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
}
