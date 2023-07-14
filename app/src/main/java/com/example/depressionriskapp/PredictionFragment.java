package com.example.depressionriskapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.depressionriskapp.databinding.FragmentPredictionPageFragmentBinding;
import com.example.depressionriskapp.databinding.UserInputFragmentBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PredictionFragment extends Fragment {

    private FragmentPredictionPageFragmentBinding binding;
   private TextView y_no_text ;
    String predictionText = "";
    private TextView prone_textview;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPredictionPageFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        y_no_text = view.findViewById(R.id.YesOrNo);
        prone_textview = view.findViewById(R.id.Depression);
        Bundle args = getArguments();
            String text = args.getString("prediction");
            System.out.println("text: "+ text);
            y_no_text.setText(text);
            if(text.equals("NO")){
                prone_textview.setText("you are not prone to depression");
            }
            else{
                prone_textview.setText("you are prone to depression");
            }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}