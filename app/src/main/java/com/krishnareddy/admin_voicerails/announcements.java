package com.krishnareddy.admin_voicerails;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class announcements extends AppCompatActivity {

    private EditText etAnnouncement;
    private Button btnGoBack, btnSend;
    private Spinner spinnerTemplates;
    private FirebaseFirestore db;
    private String phoneNumber;

    private String[] templates = {
            "1. {trainno.} {train name} is ready to leave the station.",
            "2. {trainno.} {train name} is arriving at the station.",
            "3. Kindly attention, the train number {trainno.} is coming on platform number 3.",
            "4. Kindly attention, train number {trainno.} is arriving on platform number 4 on time.",
            "5. Kindly attention, train number {trainno.} is delayed for {x} minutes.",
            "6. May I have your attention please, the train from {station name} to {station name} is cancelled due to rain."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        // Retrieve the phone number from the intent
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");

        etAnnouncement = findViewById(R.id.etAnnouncement);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnSend = findViewById(R.id.btnSend);
        spinnerTemplates = findViewById(R.id.spinnerTemplates);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, templates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTemplates.setAdapter(adapter);

        spinnerTemplates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTemplate = templates[position];
                etAnnouncement.setText(selectedTemplate.substring(selectedTemplate.indexOf(".") + 2)); // Adjust to remove numbering
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        btnGoBack.setOnClickListener(v -> {
            // Redirect to another activity
            Intent goBackIntent = new Intent(announcements.this, login.class);
            startActivity(goBackIntent);
            finish(); // Finish the current activity to prevent coming back to it with back button
        });

        btnSend.setOnClickListener(v -> {
            // Handle Send logic here
            String announcement = etAnnouncement.getText().toString();
            if (!announcement.isEmpty()) {
                sendAnnouncementToFirestore(announcement);
            } else {
                Toast.makeText(announcements.this, "Please enter an announcement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendAnnouncementToFirestore(String announcement) {
        // Create a new announcement with current timestamp and phone number
        Map<String, Object> announcementData = new HashMap<>();
        announcementData.put("announcement", announcement);
        announcementData.put("timestamp", getCurrentTime());
        announcementData.put("phoneNumber", phoneNumber);

        // Add a new document with a generated ID
        db.collection("announcements")
                .add(announcementData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(announcements.this, "Announcement sent successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(announcements.this, "Error sending announcement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private Timestamp getCurrentTime() {
        // Return current timestamp
        return new Timestamp(new Date());
    }
}



