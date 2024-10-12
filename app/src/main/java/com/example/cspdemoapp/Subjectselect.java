package com.example.cspdemoapp;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import java.sql.Date;
import java.util.Locale;
import java.util.Date;


public class Subjectselect extends AppCompatActivity {

    Button DBMS;
    Button DAA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subjectselect);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DAA = findViewById(R.id.button8);
        DBMS = findViewById(R.id.button7);

        DAA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.subject= "DAA";

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateTime = sdf.format(date);
                Log.d("Current Date and Time", currentDateTime);

                DATA.time = currentDateTime;


                Intent intent =new Intent( Subjectselect.this, ATTENDANCEACTIVITY.class);
                startActivity(intent);
                finish();
            }
        });

        DBMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.subject= "DBMS";

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateTime = sdf.format(date);
                Log.d("Current Date and Time", currentDateTime);

                DATA.time = currentDateTime;
                Intent intent =new Intent( Subjectselect.this, ATTENDANCEACTIVITY.class);
                startActivity(intent);
                finish();
            }
        });






    }
}