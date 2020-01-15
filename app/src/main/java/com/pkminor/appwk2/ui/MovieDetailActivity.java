package com.pkminor.appwk2.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.pkminor.appwk2.R;
import com.pkminor.appwk2.adapters.MoviePagerAdapter;
import com.pkminor.appwk2.models.Result;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.viewPager) ViewPager viewPager;
    private MoviePagerAdapter viewPagerAdapter;
    private List<Result> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Popular Movie Details");
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        movies = Parcels.unwrap(getIntent().getParcelableExtra("movies"));
        int startingPosition = getIntent().getIntExtra("position",0);

        viewPagerAdapter = new MoviePagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,movies);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(startingPosition);
    }
}
