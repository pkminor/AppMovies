package com.pkminor.appwk2.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pkminor.appwk2.models.Result;
import com.pkminor.appwk2.ui.MovieDetailFragment;

import java.util.List;

public class MoviePagerAdapter extends FragmentPagerAdapter {

    private List<Result> movies;

    public MoviePagerAdapter(@NonNull FragmentManager fm, int behavior,List<Result> movies) {
        super(fm, behavior);
        this.movies=movies;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //should have been onCreateFragment or getFragment to be consistent with onCreateViewHolder
        return  MovieDetailFragment.newInstance(movies.get(position));
    }

    @Override //getItemCount??
    public int getCount() {   return movies.size();  }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return movies.get(position).getOriginalTitle(); //super.getPageTitle(position);
    }
}
