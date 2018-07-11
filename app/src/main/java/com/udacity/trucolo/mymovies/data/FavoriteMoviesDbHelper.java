package com.udacity.trucolo.mymovies.data;

import com.udacity.trucolo.mymovies.data.FavoriteMoviesContract.FavoriteMovieEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "favoritemovies.db";
    private static final int DATABASE_VERSION = 4;

    public FavoriteMoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " +
                FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteMovieEntry.TITLE + " TEXT NOT NULL UNIQUE ON CONFLICT REPLACE, " +
                FavoriteMovieEntry.RATING + " REAL NOT NULL, " +
                FavoriteMovieEntry.DESCRIPTION + " TEXT, " +
                FavoriteMovieEntry.POSTER + " BLOB, " +
                FavoriteMovieEntry.ID + " TEXT " + ");";

        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
