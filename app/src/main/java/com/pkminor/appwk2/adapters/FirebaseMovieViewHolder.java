package com.pkminor.appwk2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pkminor.appwk2.Constants;
import com.pkminor.appwk2.R;
import com.pkminor.appwk2.models.Result;
import com.pkminor.appwk2.ui.MovieDetailActivity;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirebaseMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private View view;

    @BindView(R.id.movieImageView)
    ImageView movieImageView;
    @BindView(R.id.movieNameTextView)
    TextView movieNameTextView;
    @BindView(R.id.releaseDateTextView) TextView releaseDateTextView;
    @BindView(R.id.ratingTextView) TextView mRatingTextView;

    public FirebaseMovieViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);

        view = itemView;
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

        final ArrayList<Result> movies = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_MOVIES);
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    movies.add(snapshot.getValue(Result.class));
                }

                int itemPosition = getLayoutPosition();

                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                intent.putExtra("position", itemPosition );
                intent.putExtra("movies", Parcels.wrap(movies));

                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
