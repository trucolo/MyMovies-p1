package com.udacity.trucolo.mymovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.trucolo.mymovies.utilities.Movie;
import com.udacity.trucolo.mymovies.utilities.NetworkUtils;
import com.udacity.trucolo.mymovies.utilities.TheMoviesDBJsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private String POPULARITY;
    private String RATING;
    String sort;
    private String apiKey;
    int page = 1;
    private final String TAG = MainActivity.class.getSimpleName();


    @Override
    public void onClick(Movie movie) {

        Context context = this;
        Class destinationClass = MovieDetails.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(getString(R.string.movie), movie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.action_sort, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        POPULARITY = this.getString(R.string.popularity_desc);

        RATING = this.getString(R.string.average_desc);

        sort = POPULARITY;


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_main);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_display);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);

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


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){ // only when scrolling up

                    final int visibleThreshold = 2;

                    GridLayoutManager layoutManager = (GridLayoutManager)mRecyclerView.getLayoutManager();
                    int lastItem  = layoutManager.findLastCompletelyVisibleItemPosition();
                    int currentTotalCount = layoutManager.getItemCount();

                    if(currentTotalCount <= lastItem + visibleThreshold){
                        loadMovieData(sort, page, apiKey);

                    }
                }
            }
        });


        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        loadMovieData(sort, page, apiKey);

    }

    private void loadMovieData(String sort, int page, String apiKey) {
        showMovieDataView();
        new FetchMoviesTask().execute(sort, String.valueOf(page));
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            if (params.length == 0) {
                return null;
            }

            String sortCriteria = params[0];
            String pageNumber = params[1];
            Context c = getParent();
            URL moviesRequestUrl = NetworkUtils.buildUrl(sortCriteria, pageNumber, apiKey, MainActivity.this);

            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                ArrayList<Movie> moviesData = TheMoviesDBJsonUtils
                        .getMoviesFromJson(MainActivity.this, jsonMoviesResponse);

                return moviesData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Movie> movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMovieDataView();
                mMovieAdapter.setMovieData(movieData);
                page+=1;
            } else {
                showErrorMessage();
            }
        }

        private void showErrorMessage() {
            /* First, hide the currently visible data */
            mRecyclerView.setVisibility(View.INVISIBLE);
            /* Then, show the error */
            if(!isOnline()) mErrorMessageDisplay.setText(R.string.connection_error);
            else mErrorMessageDisplay.setText(R.string.general_error);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }

        private void showMovieDataView() {
            /* First, make sure the error is invisible */
            mErrorMessageDisplay.setVisibility(View.INVISIBLE);
            /* Then, make sure the movie data is visible */
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_by_popularity) {
            mMovieAdapter.setMovieData(new ArrayList<Movie>());
            resetPage();
            sort = POPULARITY;
            loadMovieData(sort, page, apiKey);
            return true;
        }

        if (id == R.id.sort_by_rating) {
            mMovieAdapter.setMovieData(new ArrayList<Movie>());
            resetPage();
            sort = RATING;
            loadMovieData(sort, page, apiKey);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void resetPage(){
        page = 1;
    }
}
