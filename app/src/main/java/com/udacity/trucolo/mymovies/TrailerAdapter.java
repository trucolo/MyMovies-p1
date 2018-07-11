package com.udacity.trucolo.mymovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.trucolo.mymovies.data.FavoriteMoviesContract;
import com.udacity.trucolo.mymovies.utilities.Movie;
import com.udacity.trucolo.mymovies.utilities.Trailer;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder>{

    private ArrayList<Trailer> mTrailersData = new ArrayList<Trailer>();
    private final TrailerAdapterOnClickHandler mClickHandler;
    private Context context;

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView mTitle;
        final ImageView mIvTrailer;

        TrailerAdapterViewHolder(View view){
            super(view);
            mIvTrailer = (ImageView) view.findViewById(R.id.iv_trailer);
            mTitle = (TextView) view.findViewById(R.id.tv_trailer_title);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Trailer trailer = mTrailersData.get(adapterPosition);
            mClickHandler.onClick(trailer);
        }
    }

    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, int position) {
        Trailer trailer = mTrailersData.get(position);
        //holder.mIvTrailer.setImageResource(R.drawable.ic_action_play);
        holder.mTitle.setText(trailer.name);
    }

    @Override
    public int getItemCount() {
        if(null == mTrailersData) return 0;
        return mTrailersData.size();
    }

    public void setTrailerData(ArrayList<Trailer> trailerData) {
        if(trailerData.isEmpty()) mTrailersData.clear();
        else mTrailersData.addAll(trailerData);
        notifyDataSetChanged();
    }

 }