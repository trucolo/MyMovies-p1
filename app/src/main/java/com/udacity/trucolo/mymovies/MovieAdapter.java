package com.udacity.trucolo.mymovies;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.trucolo.mymovies.data.FavoriteMoviesContract;
import com.udacity.trucolo.mymovies.utilities.Movie;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private ArrayList<Movie> mMoviesData = new ArrayList<Movie>();
    private final MovieAdapterOnClickHandler mClickHandler;
    private Context context;
    private Cursor mCursor;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView mVoteAverage;
        final TextView mTitle;
        final ImageView mPoster;

        MovieAdapterViewHolder(View view, @Nullable Cursor cursor){
            super(view);
            mVoteAverage = (TextView) view.findViewById(R.id.tv_vote_average);
            mTitle = (TextView) view.findViewById(R.id.tv_title);
            mPoster = (ImageView) view.findViewById(R.id.iv_poster);
            mCursor = cursor;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMoviesData.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieAdapterViewHolder(view, mCursor);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Movie movie = mMoviesData.get(position);
        if(movie.mPoster != null){
            holder.mPoster.setImageBitmap(BitmapFactory.decodeByteArray(movie.mPoster, 0, movie.mPoster.length));
        }else{
            Picasso.with(context).load(movie.posterUrl).into(holder.mPoster);
        }
        holder.mTitle.setText(movie.title);
        holder.mVoteAverage.setText(movie.average);
    }

    @Override
    public int getItemCount() {
        if(null == mMoviesData) return 0;
        return mMoviesData.size();
    }
    /**
     * Set initial movies, used when restoring the movies from other state and don't want to
     * make another call to json and hence to the internet.
     * */
    public void setInitMovieData(ArrayList<Movie> moviesData) {
        mMoviesData.clear();
        mMoviesData.addAll(moviesData);
        notifyDataSetChanged();
    }

    public void setMovieData(ArrayList<Movie> moviesData) {
        if(moviesData.isEmpty()) mMoviesData.clear();
        else mMoviesData.addAll(moviesData);
        notifyDataSetChanged();
    }
    /**
     * Used when storing the movies locally on SQLite
     * */
    public void setMovieData(Cursor cursor){
        ArrayList<Movie> favoriteMovies = new ArrayList<Movie>();
        while(cursor.moveToNext()){
            String title = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.TITLE));
            String average = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.RATING));
            String description = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.DESCRIPTION));
            byte[] poster = cursor.getBlob(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.POSTER));
            String id = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.FavoriteMovieEntry.ID));

            Movie m = new Movie(title, null, average, description, poster, id);
            favoriteMovies.add(m);
        }
        mMoviesData.clear();
        mMoviesData.addAll(favoriteMovies);
        notifyDataSetChanged();
    }

}
