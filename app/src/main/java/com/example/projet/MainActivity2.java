package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);



        View.OnClickListener ltoapn = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, Apnperformer.class);
                startActivity(intent);
                ActivityCompat.finishAffinity(MainActivity2.this);
            }
        };

        View.OnClickListener lgal = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(this,todo.class);
            }
        };

        View.OnClickListener lexit = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(-1);
            }
        };


        Button toapn = findViewById(R.id.Appareil);
        Button gal = findViewById(R.id.Galerie);
        Button exit = findViewById(R.id.Exit);

        toapn.setOnClickListener(ltoapn);
        gal.setOnClickListener(lgal);
        exit.setOnClickListener(lexit);

    }




}