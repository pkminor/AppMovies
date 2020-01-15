package com.pkminor.appwk2.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pkminor.appwk2.Constants;
import com.pkminor.appwk2.R;
import com.pkminor.appwk2.models.Result;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.movieImageView) ImageView movieImageView;
    @BindView(R.id.movieNameTextView) TextView movieNameTextView;
    @BindView(R.id.ratingTextView) TextView ratingTextView;
    @BindView(R.id.releaseDateTextView) TextView releaseDateTextView;
    @BindView(R.id.languageTextView) TextView languageTextView;
    @BindView(R.id.overviewTextView) TextView overviewTextView;
    @BindView(R.id.saveMovieButton) Button saveMovieButton;

    private Result movie;


    public MovieDetailFragment(){}

    public static MovieDetailFragment newInstance(Result movie){
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();

        Bundle args = new Bundle();
        args.putParcelable("movie", Parcels.wrap(movie));

        movieDetailFragment.setArguments(args);
        return movieDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movie = Parcels.unwrap(getArguments().getParcelable("movie"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail,container,false);
        ButterKnife.bind(this,view);

        Picasso.get().load(Constants.TMDB_IMG_BASE_URL+movie.getPosterPath()).into(movieImageView);
        movieNameTextView.setText(movie.getTitle());
        ratingTextView.setText("Popularity "+movie.getPopularity());
        releaseDateTextView.setText(movie.getReleaseDate());
        languageTextView.setText(movie.getOriginalLanguage());
        overviewTextView.setText(movie.getOverview());

        saveMovieButton.setOnClickListener(this);

        return view; //super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference refMovies = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_MOVIES)
                .child(user.getUid());

        DatabaseReference pushRef = refMovies.push();
        String pushId = pushRef.getKey();
        movie.setPushId(pushId);
        refMovies.push().setValue(movie);

        Toast.makeText(getContext(),"Saved MOVIE ",Toast.LENGTH_SHORT).show();
    }
}
