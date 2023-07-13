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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONArray;
import  org.json.JSONException;
import org.json.JSONObject;

import com.example.depressionriskapp.databinding.UserInputFragmentBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class UserInputFragment extends Fragment {
    Set<String> uniqueValues = new HashSet<>(); // Use a Set to store unique values
    String[] inputArr ;
    //UI variables
    private RadioGroup genderRB;
    private EditText ageInputText;
    private RadioGroup anxietyRB;
    private EditText gpaText;
    private Button submitButton;
    private UserInputFragmentBinding binding;
    private Spinner majorSpinner;
    private Spinner yearSpinner;
    // temp vars for ui
    String selectedGender = "";
    String ageInputted = "";
    String anxietySelected = "";
    String selectedMajor = "";
    String currYearSelected = "";
    String gpaInputted = "";
    String apiEndpoint = "http://10.0.2.2:5000/predict";
    String predictionText = "";
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = UserInputFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        majorSpinner = view.findViewById(R.id.major_select_spinner);
        yearSpinner = view.findViewById(R.id.year_select_spinner);
        genderRB  = view.findViewById(R.id.radioGroup);
        ageInputText = view.findViewById(R.id.age_input);
        anxietyRB = view.findViewById(R.id.radioGroup2);
        gpaText = view.findViewById(R.id.gpa_input);
        submitButton = view.findViewById(R.id.submit_input_button);
        List<String> columnData = new ArrayList<>();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        genderRB.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedButton = view.findViewById(checkedId);
                String selectedOption = selectedButton.getText().toString();
                selectedGender = selectedOption;
                System.out.println(selectedGender);
            }
        });

        anxietyRB.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedButton = view.findViewById(checkedId);
                String selectedOption = selectedButton.getText().toString();
                anxietySelected = selectedOption;
                System.out.println(anxietySelected);
            }
        });

        //Populate the major dropdown with values from the csv file
        try {
            Scanner scanner = new Scanner(requireContext().getAssets().open("student_mental_health.csv"));

            if (scanner.hasNextLine()) {
                String headerLine = scanner.nextLine();
                String[] headers = headerLine.split(","); // Split the header row based on the delimiter
                int columnIndex = -1;

                // Find the index of the desired column by comparing with the column name
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].equalsIgnoreCase("What is your course?")) {
                        columnIndex = i;
                        break;
                    }
                }

                // Read the corresponding column data
                while (scanner.hasNextLine()) {
                    String dataLine = scanner.nextLine();
                    String[] data = dataLine.split(","); // Split the data row based on the delimiter

                    if (columnIndex >= 0 && columnIndex < data.length) {
                        String columnValue = data[columnIndex];

                        // Check if the value is unique before adding it
                        if (!uniqueValues.contains(columnValue)) {
                            uniqueValues.add(columnValue);
                            columnData.add(columnValue);
                        }
                    }
                }
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> majorAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, columnData);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.year_array, android.R.layout.simple_spinner_item);

        majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        majorSpinner.setAdapter(majorAdapter);
        yearSpinner.setAdapter(yearAdapter);

        majorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Use the selectedItem as needed
                // For example, display it in a Toast message
                selectedMajor = selectedItem;
                System.out.println(selectedMajor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected
            }
        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Use the selectedItem as needed
                // For example, display it in a Toast message
                currYearSelected = selectedItem;
                System.out.println(currYearSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ageInputted = String.valueOf(ageInputText.getText());
                gpaInputted = String.valueOf(gpaText.getText());
                System.out.println(ageInputted);

                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n \"Gender\":\""+selectedGender+"\",\r\n \"Age\":\""+ageInputted+
                        "\",\r\n \"Major\":\""+selectedMajor+"\",\r\n \"Year\":\""+currYearSelected+
                        "\",\r\n \"Anxiety\":\""+anxietySelected+
                        "\",\r\n \"GPA\":\""+gpaInputted+
                        "\"\r\n}");
                Request request = new Request.Builder()
                        .url(apiEndpoint)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    System.out.println(
                            responseData.substring(1,2)
                    );
                    Bundle args = new Bundle();
                    predictionText =  responseData.substring(1,2);
                    System.out.println("type : "+ predictionText);
                    if(predictionText.equals( "0")){
                        predictionText = "NO";
                    }
                    else if(predictionText == "1"){
                        predictionText = "YES";
                    }
                    else{
                        predictionText = "error";
                    }
                    args.putString("prediction", predictionText);
                    System.out.println("prediction: " + predictionText);
                    NavHostFragment.findNavController(UserInputFragment.this).navigate(R.id.action_SecondFragment_to_ThirdFragment,args);

                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}