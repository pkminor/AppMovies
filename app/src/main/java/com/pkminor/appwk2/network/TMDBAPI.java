package com.pkminor.appwk2.network;

import com.pkminor.appwk2.models.PopularMoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface TMDBAPI {


    @GET("movie/popular")
    Call<PopularMoviesResponse> getPopularMovies(
            @Query("page") String page,
            @Query("language") String language,
            @Header("Authorization") String authorization
    );
}
