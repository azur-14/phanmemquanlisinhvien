package com.example.giuaky;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button btnLogout;
    private TextView welcomeText;
    private Button btnUserAccountManagement;
    private Button btnStudentManagement;
    private Button btnUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcomeText);
        btnUserAccountManagement = findViewById(R.id.btnUserAccountManagement);
        btnStudentManagement = findViewById(R.id.btnStudentManagement);
        btnUserProfile = findViewById(R.id.btnUserProfile);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();

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
                mAuth.signOut(); // Sign out from Firebase
                Log.d("MainActivity", "User signed out");
                SignOut();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart called");
        if (mAuth.getCurrentUser() != null) {
            getUserRole();
        } else {
            SignOut();
        }
    }

    private void getUserRole() {
        DbQuery.getRole().addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer role) {
                Log.d("MainActivity", "Role retrieved: " + role); // Log the retrieved role

                // Hiển thị thông báo chào mừng và quản lý nút dựa trên vai trò
                switch (role) {
                    case 2: // Admin
                        welcomeText.setText("Welcome, Admin");
                        btnUserAccountManagement.setVisibility(View.VISIBLE);
                        btnStudentManagement.setVisibility(View.VISIBLE);
                        break;
                    case 1: // Manager
                        welcomeText.setText("Welcome, Manager");
                        btnStudentManagement.setVisibility(View.VISIBLE);
                        btnUserAccountManagement.setVisibility(View.GONE); // Ẩn nút quản lý người dùng
                        break;
                    case 0: // Employee
                    default:
                        welcomeText.setText("Welcome, Employee");
                        btnUserAccountManagement.setVisibility(View.GONE); // Ẩn nút quản lý người dùng
                        btnStudentManagement.setVisibility(View.GONE); // Ẩn nút quản lý sinh viên
                        break;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Ẩn tất cả nút nếu không lấy được vai trò
                btnUserAccountManagement.setVisibility(View.GONE);
                btnStudentManagement.setVisibility(View.GONE);
                Log.e("UserRole", "Failed to get user role", e);
                Toast.makeText(MainActivity.this, "Error retrieving user role", Toast.LENGTH_SHORT).show(); // Thông báo lỗi cho người dùng
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
        Intent intent = new Intent(MainActivity.this, UserActivity.class);
        startActivity(intent);
    }

    public void startStudentManagementActivity() {
        Intent intent = new Intent(MainActivity.this, StudentManagementActivity.class);
        startActivity(intent);
    }

    public void startUserProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}