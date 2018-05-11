package com.udacity.trucolo.mymovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.trucolo.mymovies.utilities.Movie;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private ArrayList<Movie> mMoviesData = new ArrayList<Movie>();
    private final MovieAdapterOnClickHandler mClickHandler;
    private Context context;

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

        MovieAdapterViewHolder(View view){
            super(view);
            mVoteAverage = (TextView) view.findViewById(R.id.tv_vote_average);
            mTitle = (TextView) view.findViewById(R.id.tv_title);
            mPoster = (ImageView) view.findViewById(R.id.iv_poster);
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
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Movie movie = mMoviesData.get(position);
        Picasso.with(context).load(movie.getPosterUrl()).into(holder.mPoster);
        holder.mTitle.setText(movie.getTitle());
        holder.mVoteAverage.setText(movie.getAverage());
    }

    @Override
    public int getItemCount() {
        if(null == mMoviesData) return 0;
        return mMoviesData.size();
    }

    public void setMovieData(ArrayList<Movie> moviesData) {
        if(moviesData.isEmpty()) mMoviesData.clear();
        else mMoviesData.addAll(moviesData);
        notifyDataSetChanged();
    }

}
