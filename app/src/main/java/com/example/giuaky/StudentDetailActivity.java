package com.example.giuaky;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class StudentDetailActivity extends AppCompatActivity {
    private TextView studentName, studentId, studentEmail, studentPhone, studentAge, studentFaculty, studentMajor;
    private RecyclerView recyclerViewCertificates;
    private CertificateAdapter certificateAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Khởi tạo views
        studentName = findViewById(R.id.student_name);
        studentId = findViewById(R.id.student_id);
        studentEmail = findViewById(R.id.student_email);
        studentPhone = findViewById(R.id.student_phone);
        studentAge = findViewById(R.id.student_age);
        studentFaculty = findViewById(R.id.student_faculty);
        studentMajor = findViewById(R.id.student_major);
        recyclerViewCertificates = findViewById(R.id.recycler_view_certificates);

        // Lấy dữ liệu student từ Intent
        Student student = (Student) getIntent().getSerializableExtra("SELECTED_STUDENT");
        if (student != null) {
            Log.d("StudentDetailActivity", "Student loaded from intent: " + student.getName());
            populateStudentDetails(student);
            loadCertificates(student.getStudentID()); // Tải chứng chỉ cho sinh viên từ Firestore
        } else {
            Log.e("StudentDetailActivity", "No student found in intent");
        }
    }

    private void populateStudentDetails(Student student) {
        Log.d("StudentDetailActivity", "Populating student details for: " + student.getName());
        studentName.setText(student.getName());
        studentId.setText("ID: " + student.getStudentID());
        studentEmail.setText("Email: " + student.getEmail());
        studentPhone.setText("Phone: " + student.getPhoneNumber());
        studentAge.setText("Age: " + student.getAge());
        studentFaculty.setText("Faculty: " + student.getFaculty());
        studentMajor.setText("Major: " + student.getMajor());
    }

    private void loadCertificates(String studentID) {
        Log.d("StudentDetailActivity", "Loading certificates for student ID: " + studentID);
        DbQuery.loadCertificates(studentID, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                Log.d("StudentDetailActivity", "Certificates loaded successfully");
                setupCertificatesList(DbQuery.certificateList);
            }

            @Override
            public void onFailure() {
                Log.e("StudentDetailActivity", "Error loading certificates");
                Toast.makeText(StudentDetailActivity.this, "Error loading certificates", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCertificatesList(List<Certificate> certificatesList) {
        if (certificatesList != null && !certificatesList.isEmpty()) {
            Log.d("StudentDetailActivity", "Setting up certificate list with " + certificatesList.size() + " items");
            certificateAdapter = new CertificateAdapter(certificatesList);
            recyclerViewCertificates.setAdapter(certificateAdapter);
            recyclerViewCertificates.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Log.e("StudentDetailActivity", "Certificate list is empty or null!");
        }
    }
}
