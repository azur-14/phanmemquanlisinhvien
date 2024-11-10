package com.example.giuaky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {
    private EditText nameEditText, studentIdEditText, studentEmailEditText, studentPhoneEditText;
    private EditText studentAgeEditText, studentFacultyEditText, studentMajorEditText;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addstudent); // Ensure this layout exists

        // Initialize input fields
        nameEditText = findViewById(R.id.nameEditText);
        studentIdEditText = findViewById(R.id.studentIdEditText);
        studentEmailEditText = findViewById(R.id.studentEmailEditText);
        studentPhoneEditText = findViewById(R.id.studentPhoneEditText);
        studentAgeEditText = findViewById(R.id.studentAgeEditText);
        studentFacultyEditText = findViewById(R.id.studentFacultyEditText);
        studentMajorEditText = findViewById(R.id.studentMajorEditText);
        addButton = findViewById(R.id.addButton);

        // Set listener for the add button
        addButton.setOnClickListener(v -> addStudent());
    }

    private void addStudent() {
        String name = nameEditText.getText().toString().trim();
        String studentId = studentIdEditText.getText().toString().trim();
        String email = studentEmailEditText.getText().toString().trim();
        String phone = studentPhoneEditText.getText().toString().trim();
        String faculty = studentFacultyEditText.getText().toString().trim();
        String major = studentMajorEditText.getText().toString().trim();
        int age;

        // Validate input data
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name.", Toast.LENGTH_SHORT).show();
            nameEditText.requestFocus();
            return;
        }

        if (studentId.isEmpty()) {
            Toast.makeText(this, "Please enter a student ID.", Toast.LENGTH_SHORT).show();
            studentIdEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_SHORT).show();
            studentEmailEditText.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number.", Toast.LENGTH_SHORT).show();
            studentPhoneEditText.requestFocus();
            return;
        }

        if (faculty.isEmpty()) {
            Toast.makeText(this, "Please enter a faculty.", Toast.LENGTH_SHORT).show();
            studentFacultyEditText.requestFocus();
            return;
        }

        if (major.isEmpty()) {
            Toast.makeText(this, "Please enter a major.", Toast.LENGTH_SHORT).show();
            studentMajorEditText.requestFocus();
            return;
        }

        if (studentAgeEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter an age.", Toast.LENGTH_SHORT).show();
            studentAgeEditText.requestFocus();
            return;
        }

        // Parse age and handle exceptions
        try {
            age = Integer.parseInt(studentAgeEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age.", Toast.LENGTH_SHORT).show();
            studentAgeEditText.requestFocus();
            return;
        }

        // Create a new Student object
        Student newStudent = new Student("student", name, email, phone, "normal", studentId, age, faculty, major);

        // Send data back to the main activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("NEW_STUDENT", newStudent);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}