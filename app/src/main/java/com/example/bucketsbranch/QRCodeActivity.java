package com.example.bucketsbranch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private TextView dataPreviewText;
    private TextView instructionsText;
    private Button saveButton;
    private Button shareButton;
    private Button closeButton;
    private Bitmap qrCodeBitmap;
    private String scoutingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        initializeViews();
        loadQRCodeData();
        setupListeners();
    }

    private void initializeViews() {
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        saveButton = findViewById(R.id.saveButton);
        shareButton = findViewById(R.id.shareButton);
        closeButton = findViewById(R.id.closeButton);
    }

    private void loadQRCodeData() {
        Intent intent = getIntent();
        scoutingData = intent.getStringExtra("data");
        byte[] byteArray = intent.getByteArrayExtra("qrBitmap");

        if (byteArray != null) {
            // If bitmap was passed as byte array, decode it
            android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
            qrCodeBitmap = android.graphics.BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
        } else {
            // If no bitmap passed, generate it here
            generateQRCodeFromData();
        }

        // Display data preview
        if (scoutingData != null && !scoutingData.isEmpty()) {
            displayDataPreview(scoutingData);
        }
    }

    private void generateQRCodeFromData() {
        if (scoutingData == null || scoutingData.isEmpty()) {
            Toast.makeText(this, "No data to generate QR code", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            com.google.zxing.qrcode.QRCodeWriter writer = new com.google.zxing.qrcode.QRCodeWriter();
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(
                    scoutingData,
                    com.google.zxing.BarcodeFormat.QR_CODE,
                    800,
                    800
            );

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            qrCodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrCodeBitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            qrCodeImageView.setImageBitmap(qrCodeBitmap);

        } catch (com.google.zxing.WriterException e) {
            Log.e("QRCodeActivity", "Error generating QR code", e);
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayDataPreview(String data) {
        // Parse and display the data in a readable format
        String[] parts = data.split(",");

        if (parts.length >= 3) {
            StringBuilder preview = new StringBuilder();
            preview.append("Scout: ").append(parts[0]).append("\n");
            preview.append("Match #: ").append(parts[1]).append("\n");
            preview.append("Team #: ").append(parts[2]).append("\n");
            preview.append("\nFull data length: ").append(data.length()).append(" characters");

            dataPreviewText.setText(preview.toString());
        } else {
            dataPreviewText.setText("Data: " + data);
        }
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveQRCodeToGallery());
        shareButton.setOnClickListener(v -> shareQRCode());
        closeButton.setOnClickListener(v -> finish());
    }

    private void saveQRCodeToGallery() {
        if (qrCodeBitmap == null) {
            Toast.makeText(this, "No QR code to save", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create a unique filename
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String fileName = "ScoutingQR_" + timeStamp + ".png";

            // Save to MediaStore (works for Android 10+)
            String savedImageURL = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    qrCodeBitmap,
                    fileName,
                    "Scouting QR Code"
            );

            if (savedImageURL != null) {
                Toast.makeText(this, "QR Code saved to gallery!", Toast.LENGTH_LONG).show();
                Log.d("QRCodeActivity", "Image saved: " + savedImageURL);
            } else {
                Toast.makeText(this, "Error saving QR code", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("QRCodeActivity", "Error saving QR code", e);
            Toast.makeText(this, "Error saving QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareQRCode() {
        if (qrCodeBitmap == null) {
            Toast.makeText(this, "No QR code to share", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Save to cache directory for sharing
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File file = new File(cachePath, "ScoutingQR_" + timeStamp + ".png");

            FileOutputStream stream = new FileOutputStream(file);
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            // Get content URI using FileProvider
            Uri contentUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );

            if (contentUri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Scouting Data QR Code");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Scouting data for match");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            }

        } catch (IOException e) {
            Log.e("QRCodeActivity", "Error sharing QR code", e);
            Toast.makeText(this, "Error sharing QR code", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}