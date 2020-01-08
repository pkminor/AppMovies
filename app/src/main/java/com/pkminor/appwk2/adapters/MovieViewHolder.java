package com.pkminor.appwk2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pkminor.appwk2.Constants;
import com.pkminor.appwk2.R;
import com.pkminor.appwk2.models.Result;
import com.pkminor.appwk2.ui.MovieDetailActivity;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.movieImageView) ImageView movieImageView;
    @BindView(R.id.movieNameTextView) TextView movieNameTextView;
    @BindView(R.id.releaseDateTextView) TextView releaseDateTextView;
    @BindView(R.id.ratingTextView) TextView mRatingTextView;

    private Context mContext;

    public MovieViewHolder(View itemView){
        super(itemView);
        ButterKnife.bind(this,itemView);

        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindMovie(Result result){
        Picasso.get().load(Constants.TMDB_IMG_BASE_URL+ result.getPosterPath()).into(movieImageView);
        movieNameTextView.setText(result.getTitle());
        releaseDateTextView.setText(result.getReleaseDate());
        mRatingTextView.setText("Popularity: "+result.getPopularity());
    }

    @Override
    public void onClick(View v) {
        int itemPosition = getLayoutPosition();
        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        intent.putExtra("position",itemPosition);
        intent.putExtra("movies", Parcels.wrap(MovieListAdapter.movies));
        mContext.startActivity(intent);
    }
}
