package com.example.emotionapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotionapp.R;
import com.example.emotionapp.activities.ResultsActivity;
import com.example.emotionapp.adapter.MyAdapter;
import com.example.emotionapp.roomdb.Quotes;
import com.example.emotionapp.viewmodel.MyViewModel;

import java.util.ArrayList;
import java.util.List;

public class PreviousResultsFragment extends Fragment {


    private final ArrayList<Quotes> quotesArrayList = new ArrayList<>();


    private Context contextNullSafe;

    private MyAdapter myAdapter;

    MyViewModel viewModel;

    private String image_url;

    Activity a;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
        if (context instanceof Activity){
            a=(Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            image_url = getArguments().getString("data");

        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_previous_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        Log.e("TAGKAAN","getcontextnullsafetycalled1");
        if (contextNullSafe == null) getContextNullSafety();
        Log.e("TAGKAAN","getcontextnullsafetycalled2");



        RecyclerView recyclerView = view.findViewById(R.id.recyclerviewfragment);

        recyclerView.setLayoutManager(new LinearLayoutManager((ResultsActivity) getActivity()));
        recyclerView.setHasFixedSize(true);

        viewModel = new ViewModelProvider(this).get(MyViewModel.class);



        viewModel.getAllQuotes().observe(getViewLifecycleOwner(), quotes -> {

            quotesArrayList.clear();

            for (Quotes c : quotes){
                quotesArrayList.add(c);
                Toast.makeText(getActivity(),"Heeeeey control point 5!"+(ResultsActivity) getActivity(), Toast.LENGTH_SHORT).show();

            }

            myAdapter.notifyDataSetChanged();

        });

        myAdapter = new MyAdapter(quotesArrayList,contextNullSafe);

        recyclerView.setAdapter(myAdapter);
        Toast.makeText(getActivity(),"Heeeeey control point 6!"+quotesArrayList.size(), Toast.LENGTH_SHORT).show();


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Quotes c = quotesArrayList.get(viewHolder.getAdapterPosition());
                viewModel.deleteQuotes(c);

            }
        }).attachToRecyclerView(recyclerView);

    }

    //I got a few nonsense mistakes and this method for just be sure about every step
    public Context getContextNullSafety() {
        Log.e("TAGKAAN","getcontextnullsafetycalled");
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }

}