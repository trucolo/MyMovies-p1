package com.udacity.trucolo.mymovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.trucolo.mymovies.utilities.Movie;

public class MovieDetails extends AppCompatActivity {

    private ImageView mPoster;
    private ImageView mSmallPoster;
    private TextView mSynopsis;
    private TextView mRating;
    private TextView mMovieTitle;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mSmallPoster = (ImageView) findViewById(R.id.iv_small_poster);
        mPoster = (ImageView) findViewById(R.id.iv_poster_details);
        mSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        mRating = (TextView) findViewById(R.id.tv_rating);
        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        Movie movie;

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity != null){
            if((movie = (Movie) intentThatStartedThisActivity.getSerializableExtra("Movie")) != null){
                Picasso.with(this).load(movie.getPosterUrl()).into(mPoster);
                Picasso.with(this).load(movie.getPosterUrl()).into(mSmallPoster);
               mSynopsis.setText(movie.getDescription());
               mRating.setText(movie.getAverage());
               mMovieTitle.setText(movie.getTitle());
            }
        }

    }
}
