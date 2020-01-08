package com.pkminor.appwk2.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pkminor.appwk2.Constants;
import com.pkminor.appwk2.R;
import com.pkminor.appwk2.adapters.MovieListAdapter;
import com.pkminor.appwk2.models.PopularMoviesResponse;
import com.pkminor.appwk2.models.Result;
import com.pkminor.appwk2.network.TMDBAPI;
import com.pkminor.appwk2.network.TMDBClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.errorTextView) TextView errorTextView;
    private List<Result> popularMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Popular Movies");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        TMDBAPI tmdbapiClient = TMDBClient.getClient();
        Call<PopularMoviesResponse> call = tmdbapiClient.getPopularMovies("1","en-US", Constants.TMDB_API_KEY);

        call.enqueue(new Callback<PopularMoviesResponse>() {
            @Override
            public void onResponse(Call<PopularMoviesResponse> call, Response<PopularMoviesResponse> response) {
                hideProgressBar();

                if (response.isSuccessful()){

                    popularMovies = response.body().getResults();

                    MovieListAdapter adapter = new MovieListAdapter(MainActivity.this,popularMovies);
                    recyclerView.setAdapter(adapter);

                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                    showRestaurants();

                }else{
                    showUnsuccessfulMessage();
                }


            }

            @Override
            public void onFailure(Call<PopularMoviesResponse> call, Throwable t) {
                hideProgressBar();
                showFailureMessage();
                Log.e("ERR: ",t.getMessage());
            }
        });
    }

    private void showFailureMessage() {
        errorTextView.setText("Something went wrong. Please check your Internet connection and try again later");
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void showUnsuccessfulMessage() {
        errorTextView.setText("Something went wrong. Please try again later");
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void showRestaurants() {  recyclerView.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
