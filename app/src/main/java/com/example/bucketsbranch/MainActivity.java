package com.example.bucketsbranch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ScoutingAppPrefs";
    private static final String THEME_KEY = "theme_mode";

    // Color constants for dark mode
    private static final int DARK_BG = 0xFF0D0603;
    private static final int DARK_SURFACE = 0xFF1A1410;
    private static final int DARK_TEXT = 0xFFFF8C42;
    private static final int DARK_HINT = 0xFFAA6A35;
    private static final int DARK_SEPARATOR = 0xFF4A2F1F;

    // Color constants for light mode
    private static final int LIGHT_BG = 0xFFFFFFFF;
    private static final int LIGHT_SURFACE = 0xFFF5F5F5;
    private static final int LIGHT_TEXT = 0xFF1A1410;
    private static final int LIGHT_HINT = 0xFF888888;
    private static final int LIGHT_SEPARATOR = 0xFFDDDDDD;

    private boolean isDarkMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Apply saved theme before setting content view
        applyTheme();

        setContentView(R.layout.activity_main);

        // Apply theme colors to all views
        applyThemeColors();

        // Setup all button click listeners
        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if theme was changed in settings
        String currentTheme = sharedPreferences.getString(THEME_KEY, "dark");
        boolean currentDarkMode = !currentTheme.equals("light");

        if (currentDarkMode != isDarkMode) {
            // Theme changed, restart activity
            recreate();
        }
    }

    /**
     * Apply theme globally using AppCompat
     */
    private void applyTheme() {
        String savedTheme = sharedPreferences.getString(THEME_KEY, "dark");
        isDarkMode = !savedTheme.equals("light");

        if (isDarkMode) {
            setTheme(R.style.Theme_BucketsBranch_Dark);
        } else {
            setTheme(R.style.Theme_BucketsBranch_Light);
        }
    }

    /**
     * Apply theme colors to all views in the layout
     */
    private void applyThemeColors() {
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            applyThemeToViewRecursive(rootView);
        }
    }

    /**
     * Recursively apply theme colors to all views
     */
    private void applyThemeToViewRecursive(View view) {
        int bgColor = isDarkMode ? DARK_BG : LIGHT_BG;
        int textColor = isDarkMode ? DARK_TEXT : LIGHT_TEXT;
        int hintColor = isDarkMode ? DARK_HINT : LIGHT_HINT;
        int surfaceColor = isDarkMode ? DARK_SURFACE : LIGHT_SURFACE;
        int separatorColor = isDarkMode ? DARK_SEPARATOR : LIGHT_SEPARATOR;

        // Apply to ScrollView
        if (view instanceof ScrollView) {
            view.setBackgroundColor(bgColor);
        }
        // Apply to LinearLayout
        else if (view instanceof LinearLayout) {
            view.setBackgroundColor(bgColor);
        }
        // Apply to TextViews (but not RadioButtons or CheckBoxes)
        else if (view instanceof TextView && !(view instanceof RadioButton) && !(view instanceof CheckBox)) {
            ((TextView) view).setTextColor(textColor);
        }
        // Apply to EditTexts
        else if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setTextColor(textColor);
            editText.setHintTextColor(hintColor);
            editText.setBackgroundColor(surfaceColor);
        }
        // Apply to RadioButtons
        else if (view instanceof RadioButton) {
            RadioButton radioButton = (RadioButton) view;
            radioButton.setTextColor(textColor);
            // Note: Radio button tint is set in XML, may need dynamic update
        }
        // Apply to CheckBoxes
        else if (view instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) view;
            checkBox.setTextColor(textColor);
            // Note: Checkbox tint is set in XML, may need dynamic update
        }
        // Apply to MaterialButtons
        else if (view instanceof MaterialButton) {
            MaterialButton button = (MaterialButton) view;
            button.setTextColor(isDarkMode ? 0xFF0D0603 : 0xFFFFFFFF);
            button.setBackgroundColor(0xFFFF8C42);
        }
        // Apply to ImageButtons
        else if (view instanceof ImageButton) {
            ImageButton imgBtn = (ImageButton) view;
            imgBtn.setColorFilter(textColor, android.graphics.PorterDuff.Mode.SRC_IN);
        }

        // Recursively apply to child views
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);

                // Check if this is a separator line and apply separator color
                if (childView.getHeight() <= 3 && childView.getBackground() != null) {
                    childView.setBackgroundColor(separatorColor);
                }

                applyThemeToViewRecursive(childView);
            }
        }
    }

    /**
     * Setup all button click listeners
     */
    private void setupButtons() {
        // Settings button
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        if (settingsButton != null) {
            settingsButton.setOnClickListener(v -> openSettings());
        }

        // History button
        ImageButton historyButton = findViewById(R.id.historyButton);
        if (historyButton != null) {
            historyButton.setOnClickListener(v -> openHistory());
        }

        // Fuel counter buttons
        setupFuelCounters("auto");
        setupFuelCounters("tele");

        // Bump counter buttons
        setupBumpCounters("auto");
        setupBumpCounters("tele");

        // Trench counter buttons
        setupTrenchCounters("auto");
        setupTrenchCounters("tele");

        // QR Code and Submit buttons
        MaterialButton qrCodeButton = findViewById(R.id.qrCodeButton);
        if (qrCodeButton != null) {
            qrCodeButton.setOnClickListener(v -> generateQRCode());
        }

        MaterialButton submitButton = findViewById(R.id.submitButton);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> onSubmit());
        }
    }

    /**
     * Setup fuel counter buttons for a specific phase (auto or tele)
     */
    private void setupFuelCounters(String phase) {
        try {
            String prefix = phase.equals("auto") ? "autoFuel" : "teleFuel";

            // Get button and text view IDs
            int minus3Id = getResources().getIdentifier(prefix + "Minus3", "id", getPackageName());
            int minus1Id = getResources().getIdentifier(prefix + "Minus1", "id", getPackageName());
            int plus1Id = getResources().getIdentifier(prefix + "Plus1", "id", getPackageName());
            int plus3Id = getResources().getIdentifier(prefix + "Plus3", "id", getPackageName());
            int displayId = getResources().getIdentifier(prefix + "1", "id", getPackageName());

            MaterialButton minusBtn3 = findViewById(minus3Id);
            MaterialButton minusBtn1 = findViewById(minus1Id);
            MaterialButton plusBtn1 = findViewById(plus1Id);
            MaterialButton plusBtn3 = findViewById(plus3Id);
            TextView display = findViewById(displayId);

            if (minusBtn3 != null && display != null) {
                minusBtn3.setOnClickListener(v -> updateCounter(display, -3));
            }
            if (minusBtn1 != null && display != null) {
                minusBtn1.setOnClickListener(v -> updateCounter(display, -1));
            }
            if (plusBtn1 != null && display != null) {
                plusBtn1.setOnClickListener(v -> updateCounter(display, 1));
            }
            if (plusBtn3 != null && display != null) {
                plusBtn3.setOnClickListener(v -> updateCounter(display, 3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Setup bump counter buttons for a specific phase
     */
    private void setupBumpCounters(String phase) {
        try {
            String prefix = phase.equals("auto") ? "autoBump" : "teleBump";

            int plusId = getResources().getIdentifier(prefix + "Plus1", "id", getPackageName());
            int minusId = getResources().getIdentifier(prefix + "Minus1", "id", getPackageName());
            int displayId = getResources().getIdentifier(prefix + "Display", "id", getPackageName());

            MaterialButton plusBtn = findViewById(plusId);
            MaterialButton minusBtn = findViewById(minusId);
            TextView display = findViewById(displayId);

            if (plusBtn != null && display != null) {
                plusBtn.setOnClickListener(v -> updateCounter(display, 1));
            }
            if (minusBtn != null && display != null) {
                minusBtn.setOnClickListener(v -> updateCounter(display, -1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Setup trench counter buttons for a specific phase
     */
    private void setupTrenchCounters(String phase) {
        try {
            String prefix = phase.equals("auto") ? "autoTrench" : "teleTrench";

            int plusId = getResources().getIdentifier(prefix + "Plus1", "id", getPackageName());
            int minusId = getResources().getIdentifier(prefix + "Minus1", "id", getPackageName());
            int displayId = getResources().getIdentifier(prefix + (phase.equals("auto") ? "Display" : ""), "id", getPackageName());

            MaterialButton plusBtn = findViewById(plusId);
            MaterialButton minusBtn = findViewById(minusId);
            TextView display = findViewById(displayId);

            if (plusBtn != null && display != null) {
                plusBtn.setOnClickListener(v -> updateCounter(display, 1));
            }
            if (minusBtn != null && display != null) {
                minusBtn.setOnClickListener(v -> updateCounter(display, -1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update counter value
     */
    private void updateCounter(TextView display, int change) {
        try {
            int currentValue = Integer.parseInt(display.getText().toString());
            int newValue = Math.max(0, currentValue + change);
            display.setText(String.valueOf(newValue));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open settings activity
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Open history (TODO: implement)
     */
    private void openHistory() {
        // TODO: Implement history functionality
    }

    /**
     * Generate QR Code (TODO: implement)
     */
    public void generateQRCode() {
        // TODO: Implement QR code generation
    }

    /**
     * Submit form (TODO: implement)
     */
    public void onSubmit() {
        // TODO: Implement form submission
    }
}