package com.example.fragment_example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch sw = findViewById(R.id.switch1);


        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    FirstFrag firstFrag = new FirstFrag();
                    fragmentTransaction.replace(R.id.fragment_container,firstFrag);
                    fragmentTransaction.commit();

                }else {
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    secondFragment secondFragment = new secondFragment();
                    fragmentTransaction.replace(R.id.fragment_container, secondFragment);
                    fragmentTransaction.commit();

                }

            }
        });
    }
}