package com.example.bucketsbranch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup themeRadioGroup;
    private RadioGroup scoutingTypeRadioGroup;
    private MaterialButton saveSettingsButton;
    private MaterialButton backButton;
    private EditText scoutNameEditText;
    private EditText matchNumberEditText;
    private MaterialAutoCompleteTextView positionDropdown;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ScoutingAppPrefs";
    private static final String THEME_KEY = "theme_mode";
    private static final String SCOUTING_TYPE_KEY = "scouting_type";
    private static final String SCOUT_NAME_KEY = "scout_name";
    private static final String MATCH_NUMBER_KEY = "match_number";
    private static final String POSITION_KEY = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize UI elements
        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        scoutingTypeRadioGroup = findViewById(R.id.scoutingTypeRadioGroup);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        backButton = findViewById(R.id.backButton);
        scoutNameEditText = findViewById(R.id.scoutNameEditText);
        matchNumberEditText = findViewById(R.id.matchNumberEditText);
        positionDropdown = findViewById(R.id.positionDropdown);

        // Setup position dropdown
        setupPositionDropdown();

        // Load saved settings
        loadSettings();

        // Handle save button
        saveSettingsButton.setOnClickListener(v -> saveSettings());

        // Handle back button
        backButton.setOnClickListener(v -> finish());
    }

    private void loadSettings() {
        // Load theme preference
        String savedTheme = sharedPreferences.getString(THEME_KEY, "dark");
        if (savedTheme.equals("light")) {
            findViewById(R.id.lightModeRadio).performClick();
        } else {
            findViewById(R.id.darkModeRadio).performClick();
        }

        // Load scouting type preference
        String savedScoutingType = sharedPreferences.getString(SCOUTING_TYPE_KEY, "buckets");
        switch (savedScoutingType) {
            case "manual":
                findViewById(R.id.manualRadio).performClick();
                break;
            case "slider":
                findViewById(R.id.sliderRadio).performClick();
                break;
            case "buckets":
            default:
                findViewById(R.id.bucketsRadio).performClick();
                break;
        }

        // Load scout information
        String savedScoutName = sharedPreferences.getString(SCOUT_NAME_KEY, "");
        String savedMatchNumber = sharedPreferences.getString(MATCH_NUMBER_KEY, "");
        String savedPosition = sharedPreferences.getString(POSITION_KEY, "");

        scoutNameEditText.setText(savedScoutName);
        matchNumberEditText.setText(savedMatchNumber);
        if (!savedPosition.isEmpty()) {
            positionDropdown.setText(savedPosition, false);
        }
    }

    private void setupPositionDropdown() {
        String[] positions = {
                "Center",
                "Left",
                "Right",
                "Back",
                "Not Applicable"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                positions
        );

        positionDropdown.setAdapter(adapter);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save theme preference
        int selectedThemeId = themeRadioGroup.getCheckedRadioButtonId();
        if (selectedThemeId == R.id.darkModeRadio) {
            editor.putString(THEME_KEY, "dark");
        } else if (selectedThemeId == R.id.lightModeRadio) {
            editor.putString(THEME_KEY, "light");
        }

        // Save scouting type preference
        int selectedScoutingTypeId = scoutingTypeRadioGroup.getCheckedRadioButtonId();
        if (selectedScoutingTypeId == R.id.manualRadio) {
            editor.putString(SCOUTING_TYPE_KEY, "manual");
        } else if (selectedScoutingTypeId == R.id.sliderRadio) {
            editor.putString(SCOUTING_TYPE_KEY, "slider");
        } else {
            editor.putString(SCOUTING_TYPE_KEY, "buckets");
        }

        // Save scout information
        editor.putString(SCOUT_NAME_KEY, scoutNameEditText.getText().toString());
        editor.putString(MATCH_NUMBER_KEY, matchNumberEditText.getText().toString());
        editor.putString(POSITION_KEY, positionDropdown.getText().toString());

        editor.apply();

        // Show confirmation toast
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

        // Optional: finish activity after saving
        finish();
    }

    // Public method to get saved theme preference (use in MainActivity)
    public static String getSavedTheme(SharedPreferences prefs) {
        return prefs.getString("theme_mode", "dark");
    }

    // Public method to get saved scouting type (use in MainActivity)
    public static String getSavedScoutingType(SharedPreferences prefs) {
        return prefs.getString("scouting_type", "buckets");
    }
}