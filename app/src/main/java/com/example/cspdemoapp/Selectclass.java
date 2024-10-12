package com.example.cspdemoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Selectclass extends AppCompatActivity {

    Button selectclass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selectclass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectclass = findViewById(R.id.button6);

        selectclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.Class = "S12";
                Intent intent =new Intent(Selectclass.this,Subjectselect.class);
                startActivity(intent);
                finish();
            }
        });


    }
}