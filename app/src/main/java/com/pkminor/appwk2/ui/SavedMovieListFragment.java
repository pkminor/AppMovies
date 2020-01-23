package com.pkminor.appwk2.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pkminor.appwk2.Constants;
import com.pkminor.appwk2.R;
import com.pkminor.appwk2.adapters.FirebaseMovieListAdapter;
import com.pkminor.appwk2.models.Result;
import com.pkminor.appwk2.util.OnStartDragListener;
import com.pkminor.appwk2.util.SimpleItemTouchHelperCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedMovieListFragment extends Fragment implements OnStartDragListener {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    private FirebaseMovieListAdapter mFirebaseAdapter;
    private ItemTouchHelper mItemTouchHelper;


    public SavedMovieListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_saved_movie_list, container, false);

        ButterKnife.bind(this,view);
        setupFirebaseAdapter();
        return view;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    //method is now public
    public void onDestroy() {
        super.onDestroy();
//        mFirebaseAdapter.cleanup();
//        mFirebaseAdapter.stopListening();
    }

    private void setupFirebaseAdapter(){
        Log.d("Svdr ","Frg setup adapter");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //FirebaseArrayAdapter accepts either a DatabaseReference or a Query

        DatabaseReference mRestaurantReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_MOVIES)
                .child(user.getUid());

        Query query = FirebaseDatabase.getInstance()
                .getReference(Constants.FIREBASE_CHILD_MOVIES)
                .child(user.getUid())
                .orderByChild(Constants.FIREBASE_QUERY_INDEX);


//        FirebaseRecyclerOptions<Business> options =
//                new FirebaseRecyclerOptions.Builder<Business>()
//                        .setQuery(mRestaurantReference, Business.class)
//                        .build();

        FirebaseRecyclerOptions<Result> options =
                new FirebaseRecyclerOptions.Builder<Result>()
                        .setQuery(query, Result.class)
                        .build();

//        mFirebaseAdapter = new FirebaseRestaurantListAdapter(options, mRestaurantReference, this, this);

        mFirebaseAdapter = new FirebaseMovieListAdapter(options, query, this, getActivity());

//        mFirebaseAdapter = new FirebaseRecyclerAdapter<Business, FirebaseRestaurantViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull FirebaseRestaurantViewHolder firebaseRestaurantViewHolder, int position, @NonNull Business restaurant) {
//                firebaseRestaurantViewHolder.bindRestaurant(restaurant);
//            }
//
//            @NonNull
//            @Override
//            public FirebaseRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_item_drag, parent, false);
//                return new FirebaseRestaurantViewHolder(view);
//            }
//        }; //end firebase adapter definition


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mFirebaseAdapter);


        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mFirebaseAdapter.notifyDataSetChanged();
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mFirebaseAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }

}
