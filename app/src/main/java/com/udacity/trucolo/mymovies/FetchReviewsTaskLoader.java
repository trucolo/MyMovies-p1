package com.udacity.trucolo.mymovies;

        import android.support.annotation.Nullable;
        import android.support.v4.content.AsyncTaskLoader;

        import com.udacity.trucolo.mymovies.utilities.NetworkUtils;
        import com.udacity.trucolo.mymovies.utilities.Review;
        import com.udacity.trucolo.mymovies.utilities.TheMoviesDBJsonUtils;
        import com.udacity.trucolo.mymovies.utilities.Trailer;

        import java.net.URL;
        import java.util.ArrayList;

public class FetchReviewsTaskLoader extends AsyncTaskLoader<ArrayList<Review>> {
    private String movieId;

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<Review> loadInBackground() {



        URL moviesRequestUrl = NetworkUtils.buildUrlForTrailer("4a3933db3f5c9e3920c417239f08e1c1", getContext(), movieId);

        try {
            String jsonMoviesResponse = NetworkUtils
                    .getResponseFromHttpUrl(moviesRequestUrl);

            return TheMoviesDBJsonUtils
                    .getReviewsFromJson(getContext(), jsonMoviesResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FetchReviewsTaskLoader(MovieDetails context, String movieId) {
        super(context);
        this.movieId = movieId;
    }
}