package com.example.giuaky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StudentDetailActivity extends AppCompatActivity {
    private TextView studentName, studentId, studentEmail, studentPhone, studentAge, studentFaculty, studentMajor;
    private RecyclerView recyclerViewCertificates;
    private CertificateAdapter certificateAdapter;
    private List<Certificate> certificates; // Assuming you have a Certificate class
    private Button buttonAdd, buttonEdit, buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        // Initialize views
        studentName = findViewById(R.id.student_name);
        studentId = findViewById(R.id.student_id);
        studentEmail = findViewById(R.id.student_email);
        studentPhone = findViewById(R.id.student_phone);
        studentAge = findViewById(R.id.student_age);
        studentFaculty = findViewById(R.id.student_faculty);
        studentMajor = findViewById(R.id.student_major);
        recyclerViewCertificates = findViewById(R.id.recycler_view_certificates);

        buttonAdd = findViewById(R.id.button_add);
        buttonEdit = findViewById(R.id.button_view_details); // Assuming this is the edit button
        buttonDelete = findViewById(R.id.button_delete);

        // Retrieve the student data
        Student student = (Student) getIntent().getSerializableExtra("SELECTED_STUDENT");

        if (student != null) {
            // Populate student details
            studentName.setText(student.getName());
            studentId.setText("ID: " + student.getStudentID());
            studentEmail.setText("Email: " + student.getEmail());
            studentPhone.setText("Phone: " + student.getPhoneNumber());
            studentAge.setText("Age: " + student.getAge());
            studentFaculty.setText("Faculty: " + student.getFaculty());
            studentMajor.setText("Major: " + student.getMajor());

            // Initialize certificates list and adapter
            certificates = new ArrayList<>(); // Populate this with the student's certificates if available
            certificateAdapter = new CertificateAdapter(certificates);
            recyclerViewCertificates.setAdapter(certificateAdapter);
            recyclerViewCertificates.setLayoutManager(new LinearLayoutManager(this));
        }

        // Button listeners
        buttonAdd.setOnClickListener(v -> addCertificate());
        buttonEdit.setOnClickListener(v -> editCertificate());
        buttonDelete.setOnClickListener(v -> deleteCertificate());
    }

    private void addCertificate() {
        // Logic to add a certificate
        Toast.makeText(this, "Add Certificate clicked", Toast.LENGTH_SHORT).show();
        // Start an activity or dialog to add certificate details
    }

    private void editCertificate() {
        // Logic to edit a selected certificate
        Toast.makeText(this, "Edit Certificate clicked", Toast.LENGTH_SHORT).show();
        // Implement logic to edit the selected certificate
    }

    private void deleteCertificate() {
        // Logic to delete a selected certificate
        Toast.makeText(this, "Delete Certificate clicked", Toast.LENGTH_SHORT).show();
        // Implement logic to delete the selected certificate
    }
}