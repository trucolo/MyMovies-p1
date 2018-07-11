package com.udacity.trucolo.mymovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.trucolo.mymovies.data.FavoriteMoviesContract;
import com.udacity.trucolo.mymovies.data.FavoriteMoviesDbHelper;
import com.udacity.trucolo.mymovies.utilities.Movie;
import com.udacity.trucolo.mymovies.data.FavoriteMoviesContract.FavoriteMovieEntry;
import com.udacity.trucolo.mymovies.utilities.Trailer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class MovieDetails extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler,  LoaderManager.LoaderCallbacks<Map<String, ArrayList>> {

    private ImageView mPoster;
    private ImageView mSmallPoster;
    private TextView mSynopsis;
    private TextView mRating;
    private TextView mMovieTitle;
    private ImageButton mFavoriteButton;
    private SQLiteDatabase mDb;
    private final String TAG = MovieDetails.class.getSimpleName();
    private final int SEARCH_LOADER = 10;
    private String movieId;
    RecyclerView mRecyclerViewTrailers;
    RecyclerView mRecyclerViewReviews;
    TrailerAdapter mTrailerAdapter;
    ReviewAdapter mReviewsAdapter;
    RecyclerView.LayoutManager mLayoutTrailerManager;
    RecyclerView.LayoutManager mLayoutReviewsManager;
    private String apiKey;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources resources = this.getResources();
        InputStream rawResource = resources.openRawResource(R.raw.config);
        Properties properties = new Properties();
        try{
            properties.load(rawResource);
        }catch (IOException e){
            Log.e(TAG, "API Key could not be retrieved");
        }

        apiKey = properties.getProperty(getString(R.string.api_key));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mSmallPoster = (ImageView) findViewById(R.id.iv_small_poster);
        mPoster = (ImageView) findViewById(R.id.iv_poster_details);
        mSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        Movie movie;
        FavoriteMoviesDbHelper dbHelper = new FavoriteMoviesDbHelper(this);
        mFavoriteButton = (ImageButton) findViewById(R.id.favoriteButton);

        mLayoutTrailerManager = new LinearLayoutManager(this);
        mLayoutReviewsManager= new LinearLayoutManager(this);

        //Trailer Set up
        mRecyclerViewTrailers = (RecyclerView) findViewById(R.id.recycler_view_trailers);
        mRecyclerViewTrailers.setLayoutManager(mLayoutTrailerManager);
        mRecyclerViewTrailers.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(this);
        mRecyclerViewTrailers.setAdapter(mTrailerAdapter);

        //Reviews Set up
        mRecyclerViewReviews = (RecyclerView) findViewById(R.id.recycler_view_reviews);
        mRecyclerViewReviews.setLayoutManager(mLayoutReviewsManager);
        mRecyclerViewReviews.setHasFixedSize(true);
        mReviewsAdapter = new ReviewAdapter();
        mRecyclerViewReviews.setAdapter(mReviewsAdapter);
        mRecyclerViewReviews.setNestedScrollingEnabled(false);

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity != null){
            if((movie = (Movie) intentThatStartedThisActivity.getParcelableExtra(getString(R.string.movie))) != null){
                if(movie.mPoster != null){
                    mPoster.setImageBitmap(BitmapFactory.decodeByteArray(movie.mPoster, 0, movie.mPoster.length));
                    mSmallPoster.setImageBitmap(BitmapFactory.decodeByteArray(movie.mPoster, 0, movie.mPoster.length));
                }else{
                    Picasso.with(this).load(movie.posterUrl).into(mPoster);
                    Picasso.with(this).load(movie.posterUrl).into(mSmallPoster);
                }

                mSynopsis.setText(movie.description);
                mRating.setText(movie.average);
                mMovieTitle.setText(movie.title);
            }
            movieId = movie.id;
            if(isFavorite(movieId)){
                mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
            }
            loadTrailerData(this.getResources().getString(R.string.api_key), movie.id );

        }



    }

    private void loadTrailerData(String apiKey, String movieId) {
        showTrailersDataView();
        //onCreateLoader is not being called unless we restart it
        if(getSupportLoaderManager().getLoader(SEARCH_LOADER) != null){
            getSupportLoaderManager().restartLoader(SEARCH_LOADER, null, this);
        }else{
            getSupportLoaderManager().initLoader(SEARCH_LOADER, null, this);
        }
    }

    private void showTrailersDataView() {
        mRecyclerViewTrailers.setVisibility(View.VISIBLE);
        mRecyclerViewReviews.setVisibility(View.VISIBLE);
    }

    /**
     * Remove/Add the movie from favorites list, depending on its state.
     * */
    public void toggleFavorite(View view){
        String synopsis = mSynopsis.getText().toString();
        String title = mMovieTitle.getText().toString();
        float rating = Float.parseFloat(mRating.getText().toString());
        Bitmap poster = ((BitmapDrawable)mPoster.getDrawable()).getBitmap();
        byte[] posterBlob = getBitmapAsByteArray(poster);

        if(getContentResolver().delete(FavoriteMoviesContract.CONTENT_URI.buildUpon().appendPath(movieId).build(), null, null) == 0){
            ContentValues cv = new ContentValues();
            cv.put(FavoriteMovieEntry.TITLE, title);
            cv.put(FavoriteMovieEntry.DESCRIPTION, synopsis);
            cv.put(FavoriteMovieEntry.RATING, rating);
            cv.put(FavoriteMovieEntry.POSTER, posterBlob);
            cv.put(FavoriteMovieEntry.ID, movieId);
            getContentResolver().insert(FavoriteMoviesContract.CONTENT_URI, cv);
            mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
        }else{
            mFavoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
        }


    }

    private boolean isFavorite(String movieId){
        return getContentResolver().
                query(
                        FavoriteMoviesContract.CONTENT_URI.buildUpon().appendPath(movieId).build(),
                        null,
                        FavoriteMoviesContract.FavoriteMovieEntry.ID + "=?",
                        null,
                        null).getCount() > 0;
    }

    /**
     * @param bitmap
     * @return byte[] as the representation of bitmap image
     * */
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    @NonNull
    @Override
    public Loader<Map<String, ArrayList>> onCreateLoader(int id, @Nullable Bundle args) {
        return new FetchTrailersTaskLoader(this, movieId, apiKey) ;
    }

    /**
     * Fetches the trailers and reviews for a given movie mapped by its id
     * */
    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, ArrayList>> loader, Map<String, ArrayList> trailers) {
        if (trailers != null) {
            showTrailersDataView();
            if(trailers.get("trailer") != null){
                mTrailerAdapter.setTrailerData(trailers.get("trailer"));
            }
            if(trailers.get("reviews") != null){
                mReviewsAdapter.setReviewsData(trailers.get("reviews"));
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, ArrayList>> trailers) {

    }

    /**
     * Starts the trailer either in browser or youtube, depending on user preferences.
     * */
    @Override
    public void onClick(Trailer trailer) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.link)));
    }
}
