package com.example.bucketsbranch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup themeRadioGroup;
    private MaterialButton saveSettingsButton;
    private MaterialButton backButton;
    private EditText scoutNameEditText;
    private EditText matchNumberEditText;
    private MaterialAutoCompleteTextView positionDropdown;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ScoutingAppPrefs";
    private static final String THEME_KEY = "theme_mode";
    private static final String SCOUT_NAME_KEY = "scout_name";
    private static final String MATCH_NUMBER_KEY = "match_number";
    private static final String POSITION_KEY = "position";

    private boolean themeChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved theme before setting content view
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI elements
        themeRadioGroup = findViewById(R.id.themeRadioGroup);
        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        backButton = findViewById(R.id.backButton);
        scoutNameEditText = findViewById(R.id.scoutNameInput);
        matchNumberEditText = findViewById(R.id.matchNumberInput);
        positionDropdown = findViewById(R.id.positionDropdown);

        // Setup position dropdown
        setupPositionDropdown();

        // Load saved settings
        loadSettings();

        // Track theme changes
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            themeChanged = true;
        });

        // Handle save button
        saveSettingsButton.setOnClickListener(v -> saveSettings());

        // Handle back button
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Apply theme globally using AppCompat theme
     */
    private void applyTheme() {
        String savedTheme = sharedPreferences.getString(THEME_KEY, "dark");

        if (savedTheme.equals("light")) {
            setTheme(R.style.Theme_BucketsBranch_Light);
        } else {
            setTheme(R.style.Theme_BucketsBranch_Dark);
        }
    }

    private void loadSettings() {
        // Load theme preference
        String savedTheme = sharedPreferences.getString(THEME_KEY, "dark");
        if (savedTheme.equals("light")) {
            findViewById(R.id.lightModeRadio).performClick();
        } else {
            findViewById(R.id.darkModeRadio).performClick();
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
                "Red1",
                "Red2",
                "Red3",
                "Blue1",
                "Blue2",
                "Blue3"
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
        String newTheme;
        if (selectedThemeId == R.id.darkModeRadio) {
            newTheme = "dark";
        } else {
            newTheme = "light";
        }
        editor.putString(THEME_KEY, newTheme);

        // Save scout information
        editor.putString(SCOUT_NAME_KEY, scoutNameEditText.getText().toString());
        editor.putString(MATCH_NUMBER_KEY, matchNumberEditText.getText().toString());
        editor.putString(POSITION_KEY, positionDropdown.getText().toString());

        editor.apply();

        // Show confirmation toast
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

        // If theme changed, restart the activity to apply the new theme
        if (themeChanged) {
            // Finish and let MainActivity apply the new theme when user returns
            finish();
            // Optional: startActivity(new Intent(this, MainActivity.class));
        } else {
            finish();
        }
    }

    /**
     * Static method to get saved theme (use in MainActivity)
     */
    public static String getSavedTheme(SharedPreferences prefs) {
        return prefs.getString("theme_mode", "dark");
    }
}