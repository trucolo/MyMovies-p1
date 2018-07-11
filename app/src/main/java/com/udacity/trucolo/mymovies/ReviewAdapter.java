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
import com.udacity.trucolo.mymovies.utilities.Review;
import com.udacity.trucolo.mymovies.utilities.Trailer;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{

    private ArrayList<Review> mReviewsData = new ArrayList<Review>();
    private Context context;


    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        final TextView mTvAuthorReview ;
        final TextView mTvAuthorContent;

        ReviewAdapterViewHolder(View view){
            super(view);
            mTvAuthorReview = (TextView) view.findViewById(R.id.tv_review_author);
            mTvAuthorContent = (TextView) view.findViewById(R.id.tv_review_content);
        }

    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        Review review = mReviewsData.get(position);
        holder.mTvAuthorReview.setText(review.author);
        holder.mTvAuthorContent.setText(review.content);
    }

    @Override
    public int getItemCount() {
        if(null == mReviewsData) return 0;
        return mReviewsData.size();
    }

    public void setReviewsData(ArrayList<Review> reviewsData) {
        if(reviewsData.isEmpty()) mReviewsData.clear();
        else mReviewsData.addAll(reviewsData);
        notifyDataSetChanged();
    }

}