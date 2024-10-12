package com.example.cspdemoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    int OUTPUT_SIZE = 192;
    Button admin,attendance , loadfirebase;
    Context context=MainActivity.this;

    private HashMap<String, Classifier.Recognition> registered = new HashMap<>(); //saved Faces



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        admin = findViewById(R.id.button);
        attendance= findViewById(R.id.button2);
        loadfirebase = findViewById(R.id.button4);




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,ADMINACTIVITY.class);
                startActivity(intent);
                finish();
            }
        });

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,Selectclass.class);
                startActivity(intent);
                finish();
            }
        });

        loadfirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveDataFromFirebase();
            }
        });









    }


    //Save Faces to Shared Preferences.Conversion of Recognition objects to json string
    private void insertToSP(HashMap<String, Classifier.Recognition> jsonMap, int mode) {
        if(mode==1)  //mode: 0:save all, 1:clear all, 2:update all
            jsonMap.clear();
        else if (mode==0)
            jsonMap.putAll(readFromSP());
        String jsonString = new Gson().toJson(jsonMap);

        Log.d("data stores" , jsonString);
//        for (Map.Entry<String, Classifier.Recognition> entry : jsonMap.entrySet())
//        {
//            System.out.println("Entry Input "+entry.getKey()+" "+  entry.getValue().getExtra());
//        }
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("map", jsonString);
        //System.out.println("Input josn"+jsonString.toString());
        editor.apply();
        Toast.makeText(context, "Recognitions Saved", Toast.LENGTH_SHORT).show();
    }

    //Load Faces from Shared Preferences.Json String to Recognition object
    private HashMap<String, Classifier.Recognition> readFromSP() {
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, Classifier.Recognition>());
        String json = sharedPreferences.getString("map", defValue);
        Log.d("Data Store", json);

        TypeToken<HashMap<String, Classifier.Recognition>> token = new TypeToken<HashMap<String, Classifier.Recognition>>() {};
        HashMap<String, Classifier.Recognition> retrievedMap = new Gson().fromJson(json, token.getType());

        // During type conversion and save/load procedure, format changes (e.g., float converted to double).
        // So embeddings need to be extracted from it in the required format (e.g., double to float).
        for (Map.Entry<String, Classifier.Recognition> entry : retrievedMap.entrySet()) {
            float[][] output = new float[1][OUTPUT_SIZE];
            ArrayList arrayList = (ArrayList) entry.getValue().getExtra();
            arrayList = (ArrayList) arrayList.get(0);
            for (int counter = 0; counter < arrayList.size(); counter++) {
                output[0][counter] = ((Double) arrayList.get(counter)).floatValue();
            }
            entry.getValue().setExtra(output);

            // Printing the data in a readable format
            Log.d("Recognition Entry", "Key: " + entry.getKey() +
                    ", ID: " + entry.getValue().getId() +
                    ", Title: " + entry.getValue().getTitle() +
                    ", Distance: " + (entry.getValue().distance != null ? String.format("%.2f", entry.getValue().distance) : "N/A") +
                    ", Extra (Embeddings): " + Arrays.deepToString((float[][]) entry.getValue().getExtra()));
        }

        Toast.makeText(context, "Recognitions Loaded", Toast.LENGTH_SHORT).show();


        return retrievedMap;
    }





    // Method to retrieve data from Firebase Realtime Database and save it to SharedPreferences
    private void retrieveDataFromFirebase() {
        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("recognitions");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    // Check if data exists in Firebase
                    if (dataSnapshot.exists()) {
                        // Get the JSON data from Firebase
                        String jsonData = dataSnapshot.getValue(String.class);

                        if (jsonData != null) {
                            // Convert the JSON data back to a HashMap using Gson
                            TypeToken<HashMap<String, Classifier.Recognition>> token = new TypeToken<HashMap<String, Classifier.Recognition>>() {};
                            HashMap<String, Classifier.Recognition> retrievedMap = new Gson().fromJson(jsonData, token.getType());

                            if (retrievedMap != null) {
                                // Convert ArrayList to float[][] format
                                for (Map.Entry<String, Classifier.Recognition> entry : retrievedMap.entrySet()) {
                                    Object extraData = entry.getValue().getExtra();
                                    if (extraData instanceof ArrayList) {
                                        ArrayList<ArrayList<Double>> arrayList = (ArrayList<ArrayList<Double>>) extraData;
                                        float[][] output = new float[arrayList.size()][arrayList.get(0).size()];

                                        for (int i = 0; i < arrayList.size(); i++) {
                                            for (int j = 0; j < arrayList.get(i).size(); j++) {
                                                output[i][j] = arrayList.get(i).get(j).floatValue();
                                            }
                                        }

                                        entry.getValue().setExtra(output);
                                    }

                                    // Log the retrieved data
                                    Log.d("Recognition Entry", "Key: " + entry.getKey() +
                                            ", ID: " + entry.getValue().getId() +
                                            ", Title: " + entry.getValue().getTitle() +
                                            ", Distance: " + (entry.getValue().distance != null ? String.format("%.2f", entry.getValue().distance) : "N/A") +
                                            ", Extra (Embeddings): " + Arrays.deepToString((float[][]) entry.getValue().getExtra()));
                                }

//                                clearnameList();

//                                insertToSP(registered, 1);

                                // Save the retrieved data to SharedPreferences
                                insertToSP(retrievedMap, 0); // mode 0: save all to SharedPreferences

                                registered.putAll(readFromSP());


                                Toast.makeText(context, "Data retrieved from Firebase and saved to SharedPreferences successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("FirebaseData", "Failed to convert JSON data to HashMap.");
                                Toast.makeText(context, "Data format error: Could not convert to HashMap.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("FirebaseData", "JSON data is null.");
                            Toast.makeText(context, "Data not found in Firebase.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FirebaseData", "Data snapshot does not exist.");
                        Toast.makeText(context, "No data available in Firebase.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("FirebaseDataError", "Error retrieving data: " + e.getMessage());
                    Toast.makeText(context, "Error retrieving data from Firebase.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDataError", "Database error: " + databaseError.getMessage());
                Toast.makeText(context, "Failed to retrieve data from Firebase: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}