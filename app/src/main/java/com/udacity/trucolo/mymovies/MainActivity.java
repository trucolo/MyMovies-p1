package com.udacity.trucolo.mymovies;


import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.udacity.trucolo.mymovies.utilities.Movie;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    ProgressBar mLoadingIndicator;
    RecyclerView mRecyclerView;
    MovieAdapter mMovieAdapter;
    TextView mErrorMessageDisplay;
    String POPULARITY;
    String RATING;
    String sort;
    static String apiKey;
    public int page;
    private final String TAG = MainActivity.class.getSimpleName();
    RecyclerView.LayoutManager mLayoutManager;
    private static int MOVIES_SEARCH_LOADER = 22;
    ArrayList<Movie> movieData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        page = savedInstanceState != null ? savedInstanceState.getInt(getResources().getString(R.string.page)): 1;
        POPULARITY = this.getString(R.string.popularity_desc);
        RATING = this.getString(R.string.average_desc);
        movieData = new ArrayList<Movie>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getFragmentManager();

        sort = POPULARITY;
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_main);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_display);
        mLayoutManager = new GridLayoutManager(this, numberOfColumns());

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

        //Infinite scrolling
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    // only when scrolling up
                    final int visibleThreshold = getResources().getInteger(R.integer.number_of_columns);
                    int lastVisiblePositions[];
                    GridLayoutManager layoutManager = (GridLayoutManager)mRecyclerView.getLayoutManager();

                    lastVisiblePositions = new int[visibleThreshold];
                    //sometimes due to layout options, one or more positions aren't filled up correctly, then I have to
                    //get the max of those positions to be able to continue scrolling
                    int lastItem  = layoutManager.findLastVisibleItemPosition();
                    int currentTotalCount = layoutManager.getItemCount();
                    if(currentTotalCount <= lastItem + numberOfColumns()){
                        loadMovieData();
                    }
                }
            }
        });

        if (null != savedInstanceState) {
            page = savedInstanceState.getInt(getResources().getString(R.string.page));
            sort = savedInstanceState.getString(getResources().getString(R.string.sort_by));
        }

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        showMovieDataView();
        loadMovieData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovieData();
    }

    /**
     * Used to restore the list of movies, instead of fetching them again from the Internet
     * */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(TAG, "Executando onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        movieData = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.movie));
        if (null != movieData && !movieData.isEmpty()) {
            showMovieDataView();
            mMovieAdapter.setMovieData(movieData);
            page = savedInstanceState.getInt(getResources().getString(R.string.page));
            sort = savedInstanceState.getString(getResources().getString(R.string.sort_by));
            //page+=1;
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.action_sort, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        inflater.inflate(R.menu.action_favorites, menu);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getResources().getString(R.string.movie), movieData);
        outState.putInt(getResources().getString(R.string.page), page);
        outState.putString(getResources().getString(R.string.sort_by), sort);
        super.onSaveInstanceState(outState);
    }




    private int max(int[] positions){
        int max = 0;
        for(int i = 0; i < positions.length; i++){
            if(positions[i] > max)
                max = positions[i];
        }
        return max;
    }



    private void loadMovieData() {
        if(getSupportLoaderManager().getLoader(MOVIES_SEARCH_LOADER) != null){
            getSupportLoaderManager().restartLoader(MOVIES_SEARCH_LOADER, null, this);
        }else{
            getSupportLoaderManager().initLoader(MOVIES_SEARCH_LOADER, null, this);
        }
    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);


    }

    public void resetPage(){
        page = 1;
    }

    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new FetchMoviesTaskLoader(this, sort, page, apiKey);
    }



    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Movie>> loader, ArrayList<Movie> movieData) {
        if (movieData != null) {
            this.movieData.addAll(movieData);
            mMovieAdapter.setMovieData(movieData);
            page+=1;
        } else {
            showErrorMessage();
        }

        //Accessing the UI thread to make the progress bar invisible after fetching data
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Movie>> loader) {

    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        if(!isOnline()) mErrorMessageDisplay.setText(R.string.connection_error);
        else mErrorMessageDisplay.setText(R.string.general_error);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_by_popularity) {
            mMovieAdapter.setMovieData(new ArrayList<Movie>());
            resetPage();
            sort = POPULARITY;
            loadMovieData();
            return true;
        }

        if (id == R.id.sort_by_rating) {
            mMovieAdapter.setMovieData(new ArrayList<Movie>());
            resetPage();
            sort = RATING;
            loadMovieData();
            return true;
        }

        if (id == R.id.favorites){
            Context context = this;
            Class destinationClass = FavoriteMovies.class;
            Intent intentToStartFavoritesActivity = new Intent(context, destinationClass);
            startActivity(intentToStartFavoritesActivity );
        }

        return super.onOptionsItemSelected(item);
    }


    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }
}
