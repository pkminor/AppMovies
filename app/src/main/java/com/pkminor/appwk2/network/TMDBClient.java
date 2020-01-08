package com.pkminor.appwk2.network;

import com.pkminor.appwk2.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TMDBClient {

    public static Retrofit retrofit=null;

    public static TMDBAPI getClient(){

        if(retrofit==null){
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(Constants.TMDB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(TMDBAPI.class);
    }
}
