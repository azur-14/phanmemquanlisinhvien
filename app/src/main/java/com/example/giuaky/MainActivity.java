package com.example.giuaky;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;



public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView welcomeText = findViewById(R.id.welcomeText);
        Button btnUserAccountManagement = findViewById(R.id.btnUserAccountManagement);
        Button btnStudentManagement = findViewById(R.id.btnStudentManagement);
        Button btnUserProfile = findViewById(R.id.btnUserProfile);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        DbQuery.getRole().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String role) {
                // Check if the role is "admin" and update the button visibility
                if (role.equals("admin")) {
                    welcomeText.setText("Welcome, Admin");
                    btnUserAccountManagement.setVisibility(View.VISIBLE);
                    btnStudentManagement.setVisibility(View.VISIBLE);
                } if (role.equals("manager")){
                    welcomeText.setText("Welcome, Manager");
                    btnStudentManagement.setVisibility(View.VISIBLE);
                }
                else {
                    welcomeText.setText("Welcome, Employee");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure, for example, hide the button if there's an error
                btnUserAccountManagement.setVisibility(View.GONE);
                Log.e("UserRole", "Failed to get user role", e);
            }
        });

        btnUserAccountManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUserAccountManagementActivity();
            }
        });

        btnStudentManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStudentManagementActivity();
            }
        });

        btnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUserProfileActivity();
            }
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "Logout button clicked");
                mAuth.signOut(); // Đăng xuất khỏi Firebase
                Log.d("MainActivity", "User signed out");
                SignOut();
            }
        });

    }

    public void SignOut() {
        Log.d("MainActivity", "Navigating to LoginActivity");
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        MainActivity.this.finish();
    }



    public void startUserAccountManagementActivity() {
        Intent intent = new Intent(MainActivity.this, UserAccountManagement.class);
        startActivity(intent);
    }

    public void startStudentManagementActivity() {
        Intent intent = new Intent(MainActivity.this, StudentManagementActivity.class);
        startActivity(intent);
    }

    public void startUserProfileActivity() {
        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }


}
