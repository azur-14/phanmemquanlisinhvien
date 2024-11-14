package com.example.giuaky;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddStudentActivity extends AppCompatActivity {
    private EditText nameEditText, studentIdEditText, studentEmailEditText, studentPhoneEditText;
    private EditText studentAgeEditText, studentFacultyEditText, studentMajorEditText;
    private Button addButton;
    private ImageView studentImageView;
    private Button selectImageButton;
    private String newBase64Image;
    private static final int PICK_IMAGE_REQUEST = 1;

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
        studentImageView = findViewById(R.id.studentImageView);
        selectImageButton = findViewById(R.id.selectImageButton);

        // Set listeners
        addButton.setOnClickListener(v -> addStudent());
        selectImageButton.setOnClickListener(v -> chooseImageForDialog());
    }
    private void chooseImageForDialog() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                newBase64Image = convertBitmapToBase64(bitmap); // Chuyển đổi bitmap sang Base64
                studentImageView.setImageBitmap(bitmap); // Hiển thị hình ảnh trong ImageView
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    private void addStudent() {
        String name = nameEditText.getText().toString().trim();
        String studentId = studentIdEditText.getText().toString().trim();
        String email = studentEmailEditText.getText().toString().trim();
        String phone = studentPhoneEditText.getText().toString().trim();
        String faculty = studentFacultyEditText.getText().toString().trim();
        String major = studentMajorEditText.getText().toString().trim();
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
        int age;
        try {
            age = Integer.parseInt(studentAgeEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age.", Toast.LENGTH_SHORT).show();
            studentAgeEditText.requestFocus();
            return;
        }

        // Validate input data (same as before)

        // Create a new Student object
        Student newStudent = new Student(studentId, name, faculty, major, age, phone, email, newBase64Image);

        // Send data back to the main activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("NEW_STUDENT", newStudent);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}