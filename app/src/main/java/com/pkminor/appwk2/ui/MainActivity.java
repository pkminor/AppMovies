package com.pkminor.appwk2.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class MainActivity extends AppCompatActivity implements  View.OnClickListener {

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.errorTextView) TextView errorTextView;
    @BindView(R.id.fabFavoriteMovies) FloatingActionButton floatingActionButton;
    private List<Result> popularMovies;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ValueEventListener searchListener;
    private DatabaseReference mSearchedLocationReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Popular Movies");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        floatingActionButton.setOnClickListener(this);

        mAuth= FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                getSupportActionBar().setTitle("Welcome, "+user.getDisplayName());
            }
        };

        searchListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot sp: dataSnapshot.getChildren()){
                    Log.d("DS ",sp.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        mSearchedLocationReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(Constants.FIREBASE_CHILD_SEARCHED_PAGE);

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

    //runs when the activity is halted
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //avoid wasting battery life when user exits the activity
        mSearchedLocationReference.removeEventListener(searchListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener!=null) mAuth.removeAuthStateListener(mAuthStateListener);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        ButterKnife.bind(this);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getRestaurants(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_logout:
                logout();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_logout:
                logout();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.fabFavoriteMovies:
                Intent intent = new Intent(MainActivity.this, SavedMovieListActivity.class);
                startActivity(intent);
                break;

                default:
                    break;

        }
    }

    private void showFailureMessage() {
        errorTextView.setText("Something went wrong. Please check your Internet connection and try again later");
        errorTextView.setVisibility(View.VISIBLE);
    }
    private void showUnsuccessfulMessage() {
        errorTextView.setText("Something went wrong. Please try again later");
        errorTextView.setVisibility(View.VISIBLE);
    }
    private void showRestaurants() {  recyclerView.setVisibility(View.VISIBLE); }
    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void getRestaurants(String page){
        TMDBAPI tmdbapiClient = TMDBClient.getClient();
        Call<PopularMoviesResponse> call = tmdbapiClient.getPopularMovies(page,"en-US", Constants.TMDB_API_KEY);

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

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
