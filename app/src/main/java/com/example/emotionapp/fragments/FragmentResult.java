package com.example.emotionapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.emotionapp.R;
import com.example.emotionapp.activities.ResultsActivity;
import com.google.firebase.auth.FirebaseAuth;


public class FragmentResult extends Fragment {

    String someData;
    private FirebaseAuth mAuth;

    TextView firsttit, firstcont, secondtit, secondcont, thirdtit, thirdcont;

    Button btngoresults, btngomain;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            someData = getArguments().getString("data");

        }




    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] separated = someData.split(";");

        firsttit = view.findViewById(R.id.FirstTitle);
        firstcont = view.findViewById(R.id.Firstcontent);
        secondtit = view.findViewById(R.id.SecondTitle);
        secondcont = view.findViewById(R.id.SecondContent);
        thirdtit = view.findViewById(R.id.ThirdTitle);
        thirdcont = view.findViewById(R.id.ThirdContent);

        firsttit.setText("FACE ANALYSIS");
        firstcont.setText(separated[0]);
        secondtit.setText("Emotional Analysis");
        secondcont.setText(separated[1]);
        thirdtit.setText("Personal Analysis");
        thirdcont.setText(separated[2]);


        btngoresults = view.findViewById(R.id.saveresultsbutton);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().isAnonymous()){
            btngoresults.setVisibility(View.GONE);
        }
        btngoresults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultsActivity resultsActivity = (ResultsActivity) getActivity();
                resultsActivity.imageUrltaker();
            }
        });

        btngomain = view.findViewById(R.id.backmainbtn);
        btngomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultsActivity resultsActivity = (ResultsActivity) getActivity();
                resultsActivity.sender();
            }
        });
    }
}