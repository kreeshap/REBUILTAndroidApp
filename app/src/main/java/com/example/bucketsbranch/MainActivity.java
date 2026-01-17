package com.example.bucketsbranch;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    EditText studentName;
    EditText matchNumber;
    EditText teamNumber;

    //auto
    CheckBox autoL1Hang;
    CheckBox autoBallCollector;
    TextView autoFuel1, autoFuel3, autoFuel5, autoFuel7;
    Button autoFuelPlus1, autoFuelMinus1;
    Button autoFuelPlus3, autoFuelMinus3;
    Button autoFuelPlus5, autoFuelMinus5;
    Button autoFuelPlus7, autoFuelMinus7;

    //teleop
    TextView teleFuel1, teleFuel3, teleFuel5, teleFuel7;
    Button teleFuelPlus1, teleFuelMinus1;
    Button teleFuelPlus3, teleFuelMinus3;
    Button teleFuelPlus5, teleFuelMinus5;
    Button teleFuelPlus7, teleFuelMinus7;

    //endgame
    TextView endFuel1, endFuel3, endFuel5, endFuel7;
    Button endFuelPlus1, endFuelMinus1;
    Button endFuelPlus3, endFuelMinus3;
    Button endFuelPlus5, endFuelMinus5;
    Button endFuelPlus7, endFuelMinus7;
    RadioGroup endgameRadioGroup;
    RadioButton endNoHang, endL1, endL2, endL3;

    //data
    int autoFuelCount1 = 0, autoFuelCount3 = 0, autoFuelCount5 = 0, autoFuelCount7 = 0;
    int teleFuelCount1 = 0, teleFuelCount3 = 0, teleFuelCount5 = 0, teleFuelCount7 = 0;
    int endFuelCount1 = 0, endFuelCount3 = 0, endFuelCount5 = 0, endFuelCount7 = 0;
    int endgameState = 0; // 0=no hang, 1=L1, 2=L2, 3=L3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //header
        studentName = findViewById(R.id.studentName);
        matchNumber = findViewById(R.id.matchNumber);
        teamNumber = findViewById(R.id.teamNumber);

        //auto
        autoL1Hang = findViewById(R.id.autoL1Hang);
        autoBallCollector = findViewById(R.id.autoBallCollector);
        autoFuel1 = findViewById(R.id.autoFuel1);
        autoFuel3 = findViewById(R.id.autoFuel3);
        autoFuel5 = findViewById(R.id.autoFuel5);
        autoFuel7 = findViewById(R.id.autoFuel7);
        autoFuelPlus1 = findViewById(R.id.autoFuelPlus1);
        autoFuelMinus1 = findViewById(R.id.autoFuelMinus1);
        autoFuelPlus3 = findViewById(R.id.autoFuelPlus3);
        autoFuelMinus3 = findViewById(R.id.autoFuelMinus3);
        autoFuelPlus5 = findViewById(R.id.autoFuelPlus5);
        autoFuelMinus5 = findViewById(R.id.autoFuelMinus5);
        autoFuelPlus7 = findViewById(R.id.autoFuelPlus7);
        autoFuelMinus7 = findViewById(R.id.autoFuelMinus7);

        // teleop
        teleFuel1 = findViewById(R.id.teleFuel1);
        teleFuel3 = findViewById(R.id.teleFuel3);
        teleFuel5 = findViewById(R.id.teleFuel5);
        teleFuel7 = findViewById(R.id.teleFuel7);
        teleFuelPlus1 = findViewById(R.id.teleFuelPlus1);
        teleFuelMinus1 = findViewById(R.id.teleFuelMinus1);
        teleFuelPlus3 = findViewById(R.id.teleFuelPlus3);
        teleFuelMinus3 = findViewById(R.id.teleFuelMinus3);
        teleFuelPlus5 = findViewById(R.id.teleFuelPlus5);
        teleFuelMinus5 = findViewById(R.id.teleFuelMinus5);
        teleFuelPlus7 = findViewById(R.id.teleFuelPlus7);
        teleFuelMinus7 = findViewById(R.id.teleFuelMinus7);

        //endgame
        endFuel1 = findViewById(R.id.endFuel1);
        endFuel3 = findViewById(R.id.endFuel3);
        endFuel5 = findViewById(R.id.endFuel5);
        endFuel7 = findViewById(R.id.endFuel7);
        endFuelPlus1 = findViewById(R.id.endFuelPlus1);
        endFuelMinus1 = findViewById(R.id.endFuelMinus1);
        endFuelPlus3 = findViewById(R.id.endFuelPlus3);
        endFuelMinus3 = findViewById(R.id.endFuelMinus3);
        endFuelPlus5 = findViewById(R.id.endFuelPlus5);
        endFuelMinus5 = findViewById(R.id.endFuelMinus5);
        endFuelPlus7 = findViewById(R.id.endFuelPlus7);
        endFuelMinus7 = findViewById(R.id.endFuelMinus7);
        endgameRadioGroup = findViewById(R.id.endgameRadioGroup);
        endNoHang = findViewById(R.id.endNoHang);
        endL1 = findViewById(R.id.endL1);
        endL2 = findViewById(R.id.endL2);
        endL3 = findViewById(R.id.endL3);

        // click listeners
        setAutoFuelListeners();
        setTeleFuelListeners();
        setEndFuelListeners();

        updateDisplays();
    }

 //autonomous fuel

    private void setAutoFuelListeners() {
        autoFuelPlus1.setOnClickListener(v -> autoFuelPlus(1));
        autoFuelMinus1.setOnClickListener(v -> autoFuelMinus(1));
        autoFuelPlus3.setOnClickListener(v -> autoFuelPlus(3));
        autoFuelMinus3.setOnClickListener(v -> autoFuelMinus(3));
        autoFuelPlus5.setOnClickListener(v -> autoFuelPlus(5));
        autoFuelMinus5.setOnClickListener(v -> autoFuelMinus(5));
        autoFuelPlus7.setOnClickListener(v -> autoFuelPlus(7));
        autoFuelMinus7.setOnClickListener(v -> autoFuelMinus(7));
    }

    private void autoFuelPlus(int value) {
        switch (value) {
            case 1: autoFuelCount1++; break;
            case 3: autoFuelCount3++; break;
            case 5: autoFuelCount5++; break;
            case 7: autoFuelCount7++; break;
        }
        updateDisplays();
    }

    private void autoFuelMinus(int value) {
        switch (value) {
            case 1: if (autoFuelCount1 > 0) autoFuelCount1--; break;
            case 3: if (autoFuelCount3 > 0) autoFuelCount3--; break;
            case 5: if (autoFuelCount5 > 0) autoFuelCount5--; break;
            case 7: if (autoFuelCount7 > 0) autoFuelCount7--; break;
        }
        updateDisplays();
    }

//teleop fuel

    private void setTeleFuelListeners() {
        teleFuelPlus1.setOnClickListener(v -> teleFuelPlus(1));
        teleFuelMinus1.setOnClickListener(v -> teleFuelMinus(1));
        teleFuelPlus3.setOnClickListener(v -> teleFuelPlus(3));
        teleFuelMinus3.setOnClickListener(v -> teleFuelMinus(3));
        teleFuelPlus5.setOnClickListener(v -> teleFuelPlus(5));
        teleFuelMinus5.setOnClickListener(v -> teleFuelMinus(5));
        teleFuelPlus7.setOnClickListener(v -> teleFuelPlus(7));
        teleFuelMinus7.setOnClickListener(v -> teleFuelMinus(7));
    }

    private void teleFuelPlus(int value) {
        switch (value) {
            case 1: teleFuelCount1++; break;
            case 3: teleFuelCount3++; break;
            case 5: teleFuelCount5++; break;
            case 7: teleFuelCount7++; break;
        }
        updateDisplays();
    }

    private void teleFuelMinus(int value) {
        switch (value) {
            case 1: if (teleFuelCount1 > 0) teleFuelCount1--; break;
            case 3: if (teleFuelCount3 > 0) teleFuelCount3--; break;
            case 5: if (teleFuelCount5 > 0) teleFuelCount5--; break;
            case 7: if (teleFuelCount7 > 0) teleFuelCount7--; break;
        }
        updateDisplays();
    }

//endgame fuel

    private void setEndFuelListeners() {
        endFuelPlus1.setOnClickListener(v -> endFuelPlus(1));
        endFuelMinus1.setOnClickListener(v -> endFuelMinus(1));
        endFuelPlus3.setOnClickListener(v -> endFuelPlus(3));
        endFuelMinus3.setOnClickListener(v -> endFuelMinus(3));
        endFuelPlus5.setOnClickListener(v -> endFuelPlus(5));
        endFuelMinus5.setOnClickListener(v -> endFuelMinus(5));
        endFuelPlus7.setOnClickListener(v -> endFuelPlus(7));
        endFuelMinus7.setOnClickListener(v -> endFuelMinus(7));
    }

    private void endFuelPlus(int value) {
        switch (value) {
            case 1: endFuelCount1++; break;
            case 3: endFuelCount3++; break;
            case 5: endFuelCount5++; break;
            case 7: endFuelCount7++; break;
        }
        updateDisplays();
    }

    private void endFuelMinus(int value) {
        switch (value) {
            case 1: if (endFuelCount1 > 0) endFuelCount1--; break;
            case 3: if (endFuelCount3 > 0) endFuelCount3--; break;
            case 5: if (endFuelCount5 > 0) endFuelCount5--; break;
            case 7: if (endFuelCount7 > 0) endFuelCount7--; break;
        }
        updateDisplays();
    }

//hang

    public void endgameSelect(View v) {
        if (endNoHang.isChecked()) endgameState = 0;
        else if (endL1.isChecked()) endgameState = 1;
        else if (endL2.isChecked()) endgameState = 2;
        else if (endL3.isChecked()) endgameState = 3;
    }

//ui

    private void updateDisplays() {
        autoFuel1.setText(String.valueOf(autoFuelCount1));
        autoFuel3.setText(String.valueOf(autoFuelCount3));
        autoFuel5.setText(String.valueOf(autoFuelCount5));
        autoFuel7.setText(String.valueOf(autoFuelCount7));

        teleFuel1.setText(String.valueOf(teleFuelCount1));
        teleFuel3.setText(String.valueOf(teleFuelCount3));
        teleFuel5.setText(String.valueOf(teleFuelCount5));
        teleFuel7.setText(String.valueOf(teleFuelCount7));

        endFuel1.setText(String.valueOf(endFuelCount1));
        endFuel3.setText(String.valueOf(endFuelCount3));
        endFuel5.setText(String.valueOf(endFuelCount5));
        endFuel7.setText(String.valueOf(endFuelCount7));
    }
}