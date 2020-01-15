package com.pkminor.appwk2.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pkminor.appwk2.Constants;
import com.pkminor.appwk2.R;
import com.pkminor.appwk2.adapters.FirebaseMovieViewHolder;
import com.pkminor.appwk2.models.Result;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SavedMovieListActivity extends AppCompatActivity {

    private DatabaseReference mMovieReference;
    private FirebaseRecyclerAdapter<Result, FirebaseMovieViewHolder> mFirebaseAdapter;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Favorite Movies");
        setContentView(R.layout.activity_saved_movies_list);
        ButterKnife.bind(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mMovieReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_MOVIES)
        .child(user.getUid());

        setupFirebaseAdapter();
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mFirebaseAdapter!=null) mFirebaseAdapter.stopListening();
    }

    private void setupFirebaseAdapter(){

        FirebaseRecyclerOptions<Result> options =
                new FirebaseRecyclerOptions.Builder<Result>()
                        .setQuery(mMovieReference, Result.class)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Result, FirebaseMovieViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirebaseMovieViewHolder firebaseMovieViewHolder, int position, @NonNull Result movie) {
                firebaseMovieViewHolder.bindMovie(movie);
            }

            @NonNull
            @Override
            public FirebaseMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
                return new FirebaseMovieViewHolder(view);
            }
        }; //end firebase adapter definition

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);

    }
}
