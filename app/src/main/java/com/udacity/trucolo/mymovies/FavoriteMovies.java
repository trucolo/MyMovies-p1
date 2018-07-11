package com.udacity.trucolo.mymovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.trucolo.mymovies.data.FavoriteMoviesContract;
import com.udacity.trucolo.mymovies.data.FavoriteMoviesDbHelper;
import com.udacity.trucolo.mymovies.utilities.Movie;
import com.udacity.trucolo.mymovies.utilities.NetworkUtils;
import com.udacity.trucolo.mymovies.utilities.TheMoviesDBJsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

public class FavoriteMovies extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private final String TAG = FavoriteMovies.class.getSimpleName();
    ProgressBar mLoadingIndicator;
    RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    TextView mErrorMessageDisplay;
    String sort;
    String apiKey;
    RecyclerView.LayoutManager mLayoutManager;

   @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = MovieDetails.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(getString(R.string.movie), movie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FavoriteMoviesDbHelper dbHelper = new FavoriteMoviesDbHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_main);
        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_display);
        mLayoutManager = new StaggeredGridLayoutManager(this.getResources().getInteger(R.integer.number_of_columns), StaggeredGridLayoutManager.VERTICAL);


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        Resources resources = this.getResources();
        try{
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            apiKey = properties.getProperty(getString(R.string.api_key));
        } catch (Resources.NotFoundException e){
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        loadMovieData(sort, apiKey);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovieData(sort, apiKey);
    }

    private void loadMovieData(String sort, String apiKey) {
        showMovieDataView();
        new FavoriteMovies.FetchMoviesTask().execute(sort);
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private Cursor getAllFavoritesSorted(){

        return  getContentResolver().
                query(
                        FavoriteMoviesContract.CONTENT_URI.buildUpon().build(),
                        null,
                        null,
                        null,
                        null);

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Cursor doInBackground(String... params) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            if (params.length == 0) {
                return null;
            }

            ArrayList<Movie> moviesData;
            Cursor result = getAllFavoritesSorted();
            return result;

        }
        @Override
        protected void onPostExecute(Cursor movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMovieDataView();
                mMovieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }

        private void showErrorMessage() {
            /* First, hide the currently visible data */
            mRecyclerView.setVisibility(View.INVISIBLE);
            /* Then, show the error */
            mErrorMessageDisplay.setText(R.string.general_error);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }

        private void showMovieDataView() {
            /* First, make sure the error is invisible */
            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            /* Then, make sure the movie data is visible */
            mRecyclerView.setVisibility(View.VISIBLE);
        }

    }
}
