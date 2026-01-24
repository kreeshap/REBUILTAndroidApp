package com.example.bucketsbranch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Header fields
    private EditText studentName;
    private EditText matchNumber;
    private EditText teamNumber;

    // Autonomous section
    private CheckBox autoL1Hang;
    private CheckBox autoBallCollector;
    private TextView autoFuelDisplay;
    private TextView autoBumpDisplay;
    private TextView autoTrenchDisplay;
    private MaterialButton autoFuelPlus1, autoFuelMinus1, autoFuelPlus3, autoFuelMinus3;
    private FloatingActionButton autoBumpPlus, autoBumpMinus, autoTrenchPlus, autoTrenchMinus;

    // TeleOp section
    private TextView teleFuelDisplay;
    private TextView teleBumpDisplay;
    private TextView teleTrenchDisplay;
    private MaterialButton teleFuelPlus1, teleFuelMinus1, teleFuelPlus3, teleFuelMinus3;
    private FloatingActionButton teleBumpPlus, teleBumpMinus, teleTrenchPlus, teleTrenchMinus;

    // Match Share checkboxes
    private CheckBox shift1Passing, shift1Defense, shift1Scoring, shift1Cycling;
    private CheckBox shift2Passing, shift2Defense, shift2Scoring, shift2Cycling;
    private CheckBox shift3Passing, shift3Defense, shift3Scoring, shift3Cycling;
    private CheckBox shift4Passing, shift4Defense, shift4Scoring, shift4Cycling;
    private CheckBox endgamePassingCB, endgameDefenseCB, endgameScoringCB, endgameCyclingCB;

    // Endgame section
    private RadioGroup hangRadioGroup;
    private RadioGroup positionRadioGroup;
    private RadioButton endNoHang, endL1, endL2, endL3;
    private RadioButton positionNone, positionCenter, positionLeft, positionRight, positionBack;

    // Comments
    private EditText comments;

    // Buttons
    private Button submitButton;
    private Button qrCodeButton;
    private ImageButton settingsButton;
    private ImageButton historyButton;

    // Data counters
    private int autoFuelCount = 0;
    private int autoBumpCount = 0;
    private int autoTrenchCount = 0;
    private int teleFuelCount = 0;
    private int teleBumpCount = 0;
    private int teleTrenchCount = 0;
    private int hangState = 0;
    private int positionState = 0;

    // Blue Alliance API
    private BlueAllianceAPI blueAllianceAPI;
    private static final String TBA_API_KEY = "YOUR_API_KEY_HERE";
    private static final String TBA_BASE_URL = "https://www.thebluealliance.com/";

    // Global data storage
    public static class GlobalDictionary {
        public static HashMap<String, String> historyDict = new HashMap<>();
        public static List<String> keyList = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeBlueAllianceAPI();
        initializeViews();
        setupListeners();
        updateAllDisplays();
    }

    private void initializeBlueAllianceAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TBA_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        blueAllianceAPI = retrofit.create(BlueAllianceAPI.class);
    }

    private void initializeViews() {
        // Header
        studentName = findViewById(R.id.studentName);
        matchNumber = findViewById(R.id.matchNumber);
        teamNumber = findViewById(R.id.teamNumber);

        // Top buttons
        settingsButton = findViewById(R.id.settingsButton);
        historyButton = findViewById(R.id.historyButton);

        // Autonomous
        autoL1Hang = findViewById(R.id.autoL1Hang);
        autoBallCollector = findViewById(R.id.autoBallCollector);
        autoFuelDisplay = findViewById(R.id.autoFuel1);
        autoBumpDisplay = findViewById(R.id.autoBumpDisplay);
        autoTrenchDisplay = findViewById(R.id.autoTrenchDisplay);

        // Student Name
        studentName = findViewById(R.id.studentName);
        autoFuelPlus1 = findViewById(R.id.autoFuelPlus1);
        autoFuelMinus1 = findViewById(R.id.autoFuelMinus1);
        autoFuelPlus3 = findViewById(R.id.autoFuelPlus3);
        autoFuelMinus3 = findViewById(R.id.autoFuelMinus3);
        autoBumpPlus = findViewById(R.id.autoBumpPlus1);
        autoBumpMinus = findViewById(R.id.autoBumpMinus1);
        autoTrenchPlus = findViewById(R.id.autoTrenchPlus1);
        autoTrenchMinus = findViewById(R.id.autoTrenchMinus1);

        // TeleOp
        teleFuelDisplay = findViewById(R.id.teleFuel1);
        teleBumpDisplay = findViewById(R.id.teleBumpDisplay);
        teleTrenchDisplay = findViewById(R.id.teleTrench);
        teleFuelPlus1 = findViewById(R.id.teleFuelPlus1);
        teleFuelMinus1 = findViewById(R.id.teleFuelMinus1);
        teleFuelPlus3 = findViewById(R.id.teleFuelPlus3);
        teleFuelMinus3 = findViewById(R.id.teleFuelMinus3);
        teleBumpPlus = findViewById(R.id.teleBumpPlus1);
        teleBumpMinus = findViewById(R.id.teleBumpMinus1);
        teleTrenchPlus = findViewById(R.id.teleTrenchPlus1);
        teleTrenchMinus = findViewById(R.id.teleTrenchMinus1);

        // Match Share checkboxes
        shift1Passing = findViewById(R.id.shift1_passing);
        shift1Defense = findViewById(R.id.shift1_defense);
        shift1Scoring = findViewById(R.id.shift1_scoring);
        shift1Cycling = findViewById(R.id.shift1_cycling);

        shift2Passing = findViewById(R.id.shift2_passing);
        shift2Defense = findViewById(R.id.shift2_defense);
        shift2Scoring = findViewById(R.id.shift2_scoring);
        shift2Cycling = findViewById(R.id.shift2_cycling);

        shift3Passing = findViewById(R.id.shift3_passing);
        shift3Defense = findViewById(R.id.shift3_defense);
        shift3Scoring = findViewById(R.id.shift3_scoring);
        shift3Cycling = findViewById(R.id.shift3_cycling);

        shift4Passing = findViewById(R.id.shift4_passing);
        shift4Defense = findViewById(R.id.shift4_defense);
        shift4Scoring = findViewById(R.id.shift4_scoring);
        shift4Cycling = findViewById(R.id.shift4_cycling);

        endgamePassingCB = findViewById(R.id.endgame_passing);
        endgameDefenseCB = findViewById(R.id.endgame_defense);
        endgameScoringCB = findViewById(R.id.endgame_scoring);
        endgameCyclingCB = findViewById(R.id.endgame_cycling);

        // Endgame
        hangRadioGroup = findViewById(R.id.hangRadioGroup);
        positionRadioGroup = findViewById(R.id.positionRadioGroup);
        endNoHang = findViewById(R.id.endNoHang);
        endL1 = findViewById(R.id.endL1);
        endL2 = findViewById(R.id.endL2);
        endL3 = findViewById(R.id.endL3);
        positionNone = findViewById(R.id.none);
        positionCenter = findViewById(R.id.endcenter);
        positionLeft = findViewById(R.id.endleft);
        positionRight = findViewById(R.id.endright);
        positionBack = findViewById(R.id.endback);

        // Comments
        comments = findViewById(R.id.Comments);

        // Buttons
        submitButton = findViewById(R.id.submitButton);
        qrCodeButton = findViewById(R.id.qrCodeButton);

        // Load saved settings from SharedPreferences
        loadSettingsFromPreferences();
    }

    private void loadSettingsFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("ScoutingAppPrefs", MODE_PRIVATE);
        String savedMatchNumber = prefs.getString("match_number", "");
        String savedPosition = prefs.getString("position", "");

        if (!savedMatchNumber.isEmpty()) {
            matchNumber.setText(savedMatchNumber);
        }
    }

    private void setupListeners() {
        settingsButton.setOnClickListener(v -> openSettings());
        historyButton.setOnClickListener(v -> openHistory());

        matchNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !matchNumber.getText().toString().isEmpty()) {
                String matchNum = matchNumber.getText().toString();
                String position = getSelectedPosition();
                if (!position.isEmpty()) {
                    fetchTeamNumber(matchNum, position);
                }
            }
        });

        // Autonomous fuel counters
        autoFuelPlus1.setOnClickListener(v -> modifyCounter("autoFuel", 1));
        autoFuelMinus1.setOnClickListener(v -> modifyCounter("autoFuel", -1));
        autoFuelPlus3.setOnClickListener(v -> modifyCounter("autoFuel", 3));
        autoFuelMinus3.setOnClickListener(v -> modifyCounter("autoFuel", -3));

        // Autonomous bump/trench counters
        autoBumpPlus.setOnClickListener(v -> modifyCounter("autoBump", 1));
        autoBumpMinus.setOnClickListener(v -> modifyCounter("autoBump", -1));
        autoTrenchPlus.setOnClickListener(v -> modifyCounter("autoTrench", 1));
        autoTrenchMinus.setOnClickListener(v -> modifyCounter("autoTrench", -1));

        // TeleOp fuel counters
        teleFuelPlus1.setOnClickListener(v -> modifyCounter("teleFuel", 1));
        teleFuelMinus1.setOnClickListener(v -> modifyCounter("teleFuel", -1));
        teleFuelPlus3.setOnClickListener(v -> modifyCounter("teleFuel", 3));
        teleFuelMinus3.setOnClickListener(v -> modifyCounter("teleFuel", -3));

        // TeleOp bump/trench counters
        teleBumpPlus.setOnClickListener(v -> modifyCounter("teleBump", 1));
        teleBumpMinus.setOnClickListener(v -> modifyCounter("teleBump", -1));
        teleTrenchPlus.setOnClickListener(v -> modifyCounter("teleTrench", 1));
        teleTrenchMinus.setOnClickListener(v -> modifyCounter("teleTrench", -1));

        // Hang radio group
        hangRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.endNoHang) hangState = 0;
            else if (checkedId == R.id.endL1) hangState = 1;
            else if (checkedId == R.id.endL2) hangState = 2;
            else if (checkedId == R.id.endL3) hangState = 3;
        });

        // Position radio group
        positionRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.none) positionState = 0;
            else if (checkedId == R.id.endcenter) positionState = 1;
            else if (checkedId == R.id.endleft) positionState = 2;
            else if (checkedId == R.id.endright) positionState = 3;
            else if (checkedId == R.id.endback) positionState = 4;

            if (!matchNumber.getText().toString().isEmpty()) {
                String matchNum = matchNumber.getText().toString();
                String position = getSelectedPosition();
                if (!position.isEmpty()) {
                    fetchTeamNumber(matchNum, position);
                }
            }
        });

        submitButton.setOnClickListener(v -> onSubmit());
        qrCodeButton.setOnClickListener(v -> generateQRCode());
    }

    private String getSelectedPosition() {
        int checkedId = positionRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.none) return "N/A";
        else if (checkedId == R.id.endcenter) return "Center";
        else if (checkedId == R.id.endleft) return "Left";
        else if (checkedId == R.id.endright) return "Right";
        else if (checkedId == R.id.endback) return "Back";
        return "";
    }

    private void fetchTeamNumber(String matchNum, String position) {
        String role = null;

        if (position.equals("N/A")) {
            Toast.makeText(this, "Please select a valid position", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (positionRadioGroup.getCheckedRadioButtonId()) {
            case R.id.endcenter:
                role = "Red1";
                break;
            case R.id.endleft:
                role = "Red2";
                break;
            case R.id.endright:
                role = "Red3";
                break;
            default:
                return;
        }

        String eventKey = "2024chpla";
        Call<Match> call = blueAllianceAPI.getMatch(eventKey + "_" + matchNum, TBA_API_KEY);

        call.enqueue(new Callback<Match>() {
            @Override
            public void onResponse(Call<Match> call, Response<Match> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Match match = response.body();
                    String teamNum = extractTeamFromMatch(match, role);
                    if (teamNum != null) {
                        teamNumber.setText(teamNum);
                        Toast.makeText(MainActivity.this, "Team fetched: " + teamNum, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Team not found for this position", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch match data", Toast.LENGTH_SHORT).show();
                    Log.e("BlueAlliance", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Match> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("BlueAlliance", "API Error", t);
            }
        });
    }

    private String extractTeamFromMatch(Match match, String role) {
        if (match.alliances == null) return null;

        if (role.startsWith("Red")) {
            int index = Integer.parseInt(String.valueOf(role.charAt(3))) - 1;
            if (match.alliances.red != null && match.alliances.red.teamKeys != null && index < match.alliances.red.teamKeys.size()) {
                return match.alliances.red.teamKeys.get(index).replace("frc", "");
            }
        } else if (role.startsWith("Blue")) {
            int index = Integer.parseInt(String.valueOf(role.charAt(4))) - 1;
            if (match.alliances.blue != null && match.alliances.blue.teamKeys != null && index < match.alliances.blue.teamKeys.size()) {
                return match.alliances.blue.teamKeys.get(index).replace("frc", "");
            }
        }
        return null;
    }

    private void openSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openHistory() {
        Toast.makeText(this, "History button clicked", Toast.LENGTH_SHORT).show();
    }

    private void modifyCounter(String counterType, int delta) {
        switch (counterType) {
            case "autoFuel":
                autoFuelCount = Math.max(0, autoFuelCount + delta);
                break;
            case "autoBump":
                autoBumpCount = Math.max(0, autoBumpCount + delta);
                break;
            case "autoTrench":
                autoTrenchCount = Math.max(0, autoTrenchCount + delta);
                break;
            case "teleFuel":
                teleFuelCount = Math.max(0, teleFuelCount + delta);
                break;
            case "teleBump":
                teleBumpCount = Math.max(0, teleBumpCount + delta);
                break;
            case "teleTrench":
                teleTrenchCount = Math.max(0, teleTrenchCount + delta);
                break;
        }
        updateAllDisplays();
    }

    private void updateAllDisplays() {
        autoFuelDisplay.setText(String.valueOf(autoFuelCount));
        autoBumpDisplay.setText(String.valueOf(autoBumpCount));
        autoTrenchDisplay.setText(String.valueOf(autoTrenchCount));
        teleFuelDisplay.setText(String.valueOf(teleFuelCount));
        teleBumpDisplay.setText(String.valueOf(teleBumpCount));
        teleTrenchDisplay.setText(String.valueOf(teleTrenchCount));
    }

    private boolean validateFields() {
        String missingFields = "";

        if (studentName.getText().toString().trim().isEmpty()) {
            missingFields += "\n\t• Student Name";
        }
        if (teamNumber.getText().toString().trim().isEmpty()) {
            missingFields += "\n\t• Team Number";
        }
        if (matchNumber.getText().toString().trim().isEmpty()) {
            missingFields += "\n\t• Match Number";
        }

        if (!missingFields.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Missing Information")
                    .setMessage("Please fill in the following fields:" + missingFields)
                    .setPositiveButton("OK", null)
                    .show();
            return false;
        }
        return true;
    }

    private String collectData() {
        String shift1Data = getCheckboxValue(shift1Passing) + "," +
                getCheckboxValue(shift1Defense) + "," +
                getCheckboxValue(shift1Scoring) + "," +
                getCheckboxValue(shift1Cycling);

        String shift2Data = getCheckboxValue(shift2Passing) + "," +
                getCheckboxValue(shift2Defense) + "," +
                getCheckboxValue(shift2Scoring) + "," +
                getCheckboxValue(shift2Cycling);

        String shift3Data = getCheckboxValue(shift3Passing) + "," +
                getCheckboxValue(shift3Defense) + "," +
                getCheckboxValue(shift3Scoring) + "," +
                getCheckboxValue(shift3Cycling);

        String shift4Data = getCheckboxValue(shift4Passing) + "," +
                getCheckboxValue(shift4Defense) + "," +
                getCheckboxValue(shift4Scoring) + "," +
                getCheckboxValue(shift4Cycling);

        String endgameData = getCheckboxValue(endgamePassingCB) + "," +
                getCheckboxValue(endgameDefenseCB) + "," +
                getCheckboxValue(endgameScoringCB) + "," +
                getCheckboxValue(endgameCyclingCB);

        String commentText = comments.getText().toString().trim();
        if (commentText.isEmpty()) {
            commentText = "No comments";
        }
        commentText = commentText.replace(",", ";");

        return studentName.getText().toString() + "," +
                matchNumber.getText().toString() + "," +
                teamNumber.getText().toString() + "," +
                getCheckboxValue(autoL1Hang) + "," +
                getCheckboxValue(autoBallCollector) + "," +
                autoFuelCount + "," +
                autoBumpCount + "," +
                autoTrenchCount + "," +
                teleFuelCount + "," +
                teleBumpCount + "," +
                teleTrenchCount + "," +
                shift1Data + "," +
                shift2Data + "," +
                shift3Data + "," +
                shift4Data + "," +
                endgameData + "," +
                hangState + "," +
                positionState + "," +
                commentText;
    }

    private int getCheckboxValue(CheckBox checkBox) {
        return checkBox.isChecked() ? 1 : 0;
    }

    public void onSubmit() {
        if (!validateFields()) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Submission")
                .setMessage("Are you sure you want to submit this scouting data?")
                .setPositiveButton("SUBMIT", (dialog, which) -> {
                    String data = collectData();
                    String matchKey = matchNumber.getText().toString();
                    GlobalDictionary.historyDict.put(matchKey, data);
                    GlobalDictionary.keyList.add(matchKey);

                    Log.d("ScoutingData", "Submitted: " + data);
                    Toast.makeText(MainActivity.this, "Data submitted successfully!", Toast.LENGTH_SHORT).show();
                    resetForm();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void generateQRCode() {
        if (!validateFields()) {
            return;
        }

        String data = collectData();

        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 800, 800);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent = new Intent(MainActivity.this, QRCodeActivity.class);
            intent.putExtra("qrBitmap", byteArray);
            intent.putExtra("data", data);
            startActivity(intent);

        } catch (WriterException e) {
            Log.e("QRCode", "Error generating QR code", e);
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetForm() {
        autoFuelCount = 0;
        autoBumpCount = 0;
        autoTrenchCount = 0;
        teleFuelCount = 0;
        teleBumpCount = 0;
        teleTrenchCount = 0;
        hangState = 0;
        positionState = 0;

        teamNumber.setText("");
        matchNumber.setText("");
        comments.setText("");

        autoL1Hang.setChecked(false);
        autoBallCollector.setChecked(false);

        shift1Passing.setChecked(false);
        shift1Defense.setChecked(false);
        shift1Scoring.setChecked(false);
        shift1Cycling.setChecked(false);

        shift2Passing.setChecked(false);
        shift2Defense.setChecked(false);
        shift2Scoring.setChecked(false);
        shift2Cycling.setChecked(false);

        shift3Passing.setChecked(false);
        shift3Defense.setChecked(false);
        shift3Scoring.setChecked(false);
        shift3Cycling.setChecked(false);

        shift4Passing.setChecked(false);
        shift4Defense.setChecked(false);
        shift4Scoring.setChecked(false);
        shift4Cycling.setChecked(false);

        endgamePassingCB.setChecked(false);
        endgameDefenseCB.setChecked(false);
        endgameScoringCB.setChecked(false);
        endgameCyclingCB.setChecked(false);

        hangRadioGroup.clearCheck();
        positionRadioGroup.clearCheck();

        updateAllDisplays();
    }

    // Blue Alliance API Interface
    public interface BlueAllianceAPI {
        @GET("api/v3/match/{matchKey}")
        Call<Match> getMatch(@Path("matchKey") String matchKey, @Header("X-TBA-Auth-Key") String apiKey);
    }

    // Match Model
    public static class Match {
        public Alliances alliances;
    }

    public static class Alliances {
        public Alliance red;
        public Alliance blue;
    }

    public static class Alliance {
        @SerializedName("team_keys")
        public List<String> teamKeys;
    }
}