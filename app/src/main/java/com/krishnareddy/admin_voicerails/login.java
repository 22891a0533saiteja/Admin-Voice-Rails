package com.krishnareddy.admin_voicerails;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class login extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find views
        phoneNumberEditText = findViewById(R.id.phone_number);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        // Set default country code for phone number
        phoneNumberEditText.setText("+91");

        // Set click listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered phone number and password
                String phoneNumber = phoneNumberEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Perform login validation
                if (isValidPhoneNumber(phoneNumber) && isValidPassword(password)) {
                    // Authenticate user
                    authenticateUser(phoneNumber, password);
                } else {
                    // Show error message indicating invalid credentials
                    Toast.makeText(login.this, "Invalid phone number or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for sign-up text view

    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Adjust validation to include the country code length
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber.length() == 13;
    }

    private boolean isValidPassword(String password) {
        // Implement password validation logic (example: minimum 6 characters)
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    private void authenticateUser(String phoneNumber, final String password) {
        // Log the phone number and password being used for authentication
        Log.d("Login_activity", "Authenticating user with phone: " + phoneNumber + " and password: " + password);

        // Query Firestore to find the document with the specified phone number
        db.collection("adminUsers")
                .whereEqualTo("phone", phoneNumber) // Make sure this matches the Firestore field name
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            if (result.isEmpty()) {
                                // No user found with the entered phone number
                                Toast.makeText(login.this, "No account found with this phone number", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (DocumentSnapshot document : result) {
                                // Retrieve the password stored in Firestore
                                String storedPassword = document.getString("password");
                                Log.d("Login_activity", "Stored password from Firestore: " + storedPassword);

                                if (password.equals(storedPassword)) {
                                    // Password matches, login successful
                                    Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    // Proceed to your desired activity (e.g., HomeActivity)
                                    Intent intent = new Intent(login.this, announcements.class);

                                    intent.putExtra("phoneNumber", phoneNumber);


                                    startActivity(intent);
                                    finish();
                                    return;
                                } else {
                                    Log.d("Login_activity", "Password does not match. Entered: " + password + " Stored: " + storedPassword);
                                }
                            }
                            // Password did not match for any document
                            Toast.makeText(login.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error fetching documents
                            Toast.makeText(login.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                            Log.e("Login_activity", "Error fetching user data", task.getException());
                        }
                    }
                });
    }
}
