package com.pkminor.appwk2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.pkminor.appwk2.Constants;
import com.pkminor.appwk2.R;
import com.pkminor.appwk2.models.Result;
import com.pkminor.appwk2.ui.MovieDetailActivity;
import com.pkminor.appwk2.ui.MovieDetailFragment;
import com.pkminor.appwk2.util.ItemTouchHelperAdapter;
import com.pkminor.appwk2.util.OnStartDragListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

public class FirebaseMovieListAdapter
        extends FirebaseRecyclerAdapter<Result, FirebaseMovieViewHolder>
        implements ItemTouchHelperAdapter {

    private DatabaseReference mRef;
    private OnStartDragListener mOnStartDragListener;
    private Context mContext;

    private ChildEventListener mChildEventListener;
    private ArrayList<Result> mResults = new ArrayList<>();

    private int mOrientation;

    public FirebaseMovieListAdapter(FirebaseRecyclerOptions<Result> options,
                                         Query ref,
                                         OnStartDragListener onStartDragListener,
                                         Context context) {

        super(options);
        mRef = ref.getRef();
        mOnStartDragListener = onStartDragListener;
        mContext = context;

        mChildEventListener = mRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mResults.add(dataSnapshot.getValue(Result.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull FirebaseMovieViewHolder firebaseMovieViewHolder, int position, @NonNull Result result) {

        firebaseMovieViewHolder.bindMovie(result);

        mOrientation = firebaseMovieViewHolder.itemView.getResources().getConfiguration().orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            createDetailFragment(0);
        }

        firebaseMovieViewHolder.movieImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mOnStartDragListener.onStartDrag(firebaseMovieViewHolder);
                }
                return false;
            }
        });

        firebaseMovieViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int itemPosition = firebaseMovieViewHolder.getAdapterPosition();
                if( mOrientation == Configuration.ORIENTATION_LANDSCAPE){
                    createDetailFragment(itemPosition);
                }else{
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_POSITION, itemPosition);
                    intent.putExtra(Constants.EXTRA_KEY_MOVIES, Parcels.wrap(mResults));
                    mContext.startActivity(intent);
                }
            }
        });

    }

    @NonNull
    @Override
    public FirebaseMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new FirebaseMovieViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mResults, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        setIndexInFirebase();
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        mResults.remove(position);
        getRef(position).removeValue();
    }

    @Override
    public void stopListening() {
        super.stopListening();
        mRef.removeEventListener(mChildEventListener);
    }

    private void setIndexInFirebase() {
        for (Result result : mResults) {
            int index = mResults.indexOf(result);
            Log.d("IND ",index+"");
            DatabaseReference ref = getRef(index);
            result.setIndex(Integer.toString(index));
            ref.setValue(result);
        }
    }

    private void createDetailFragment(int position) {

        // Creates new RestaurantDetailFragment with the given position:
        MovieDetailFragment detailFragment = MovieDetailFragment.newInstance(mResults, position);
        // Gathers necessary components to replace the FrameLayout in the layout with the RestaurantDetailFragment:
        FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        //  Replaces the FrameLayout with the RestaurantDetailFragment:
        ft.replace(R.id.movieDetailContainer, detailFragment);
        // Commits these changes:
        ft.commit();

    }

}
