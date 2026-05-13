package com.example.flower;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Home_Fragment extends Fragment {

    private Button btnCheckFlowers;

    public Home_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnCheckFlowers = view.findViewById(R.id.btnCheckFlowers);

        btnCheckFlowers.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FlowersListActivity.class);
            startActivity(intent);
        });

        return view;
    }
}