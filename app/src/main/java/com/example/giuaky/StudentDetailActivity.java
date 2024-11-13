package com.example.giuaky;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

public class StudentDetailActivity extends AppCompatActivity {
    private TextView studentName, studentId, studentEmail, studentPhone, studentAge, studentFaculty, studentMajor;
    private RecyclerView recyclerViewCertificates;
    private CertificateAdapter certificateAdapter;
    private Toolbar toolbar;
    private Button addButton, updateButton, deleteButton;
    private String studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        studentName = findViewById(R.id.student_name);
        studentId = findViewById(R.id.student_id);
        studentEmail = findViewById(R.id.student_email);
        studentPhone = findViewById(R.id.student_phone);
        studentAge = findViewById(R.id.student_age);
        studentFaculty = findViewById(R.id.student_faculty);
        studentMajor = findViewById(R.id.student_major);
        recyclerViewCertificates = findViewById(R.id.recycler_view_certificates);
        addButton = findViewById(R.id.button_add);
        updateButton = findViewById(R.id.button_update);
        deleteButton = findViewById(R.id.button_delete);

        // Retrieve student data
        Student student = (Student) getIntent().getSerializableExtra("SELECTED_STUDENT");
        if (student != null) {
            populateStudentDetails(student);
            studentID = student.getStudentID();
            loadCertificates(studentID);
        } else {
            Log.e("StudentDetailActivity", "No student found in intent");
        }
        Button editButton = findViewById(R.id.edithocsinhbutton);
        editButton.setOnClickListener(v -> {
            if (student != null) {
                openEditStudentDialog(student);
            } else {
                Toast.makeText(this, "Student data not found", Toast.LENGTH_SHORT).show();
            }
        });
        // Set up button listeners
        addButton.setOnClickListener(v -> {
            if (student != null) {
                openAddCertificateDialog(student);
            } else {
                Toast.makeText(this, "Student data not found", Toast.LENGTH_SHORT).show();
            }
        });
        updateButton.setOnClickListener(v -> updateSelectedCertificates());
        deleteButton.setOnClickListener(v -> deleteSelectedCertificates());
    }
    private void openEditStudentDialog(Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Student Details");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_student, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.editTextName);
        EditText emailInput = dialogView.findViewById(R.id.editTextEmail);
        EditText phoneInput = dialogView.findViewById(R.id.editTextPhone);
        EditText ageInput = dialogView.findViewById(R.id.editTextAge);
        EditText facultyInput = dialogView.findViewById(R.id.editTextFaculty);
        EditText majorInput = dialogView.findViewById(R.id.editTextMajor);

        // Điền dữ liệu hiện tại
        nameInput.setText(student.getName());
        emailInput.setText(student.getEmail());
        phoneInput.setText(student.getPhoneNumber());
        ageInput.setText(String.valueOf(student.getAge()));
        facultyInput.setText(student.getFaculty());
        majorInput.setText(student.getMajor());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedName = nameInput.getText().toString().trim();
            String updatedEmail = emailInput.getText().toString().trim();
            String updatedPhone = phoneInput.getText().toString().trim();
            String updatedAgeStr = ageInput.getText().toString().trim();
            String updatedFaculty = facultyInput.getText().toString().trim();
            String updatedMajor = majorInput.getText().toString().trim();

            // Kiểm tra dữ liệu
            if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty() || updatedAgeStr.isEmpty() || updatedFaculty.isEmpty() || updatedMajor.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int updatedAge;
            try {
                updatedAge = Integer.parseInt(updatedAgeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid age format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng sinh viên mới với thông tin đã cập nhật
            student.setName(updatedName);
            student.setEmail(updatedEmail);
            student.setPhonenumber(updatedPhone);
            student.setAge(updatedAge);
            student.setFaculty(updatedFaculty);
            student.setMajor(updatedMajor);

            // Gọi phương thức cập nhật
            DbQuery.updateStudent(student, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(StudentDetailActivity.this, "Student details updated successfully", Toast.LENGTH_SHORT).show();
                    populateStudentDetails(student); // Refresh displayed details
                }

                @Override
                public void onFailure() {
                    Toast.makeText(StudentDetailActivity.this, "Failed to update student details", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void populateStudentDetails(Student student) {
        studentName.setText(student.getName());
        studentId.setText("ID: " + student.getStudentID());
        studentEmail.setText("Email: " + student.getEmail());
        studentPhone.setText("Phone: " + student.getPhoneNumber());
        studentAge.setText("Age: " + student.getAge());
        studentFaculty.setText("Faculty: " + student.getFaculty());
        studentMajor.setText("Major: " + student.getMajor());
    }

    private void openAddCertificateDialog(Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Certificate");

        View dialogView = getLayoutInflater().inflate(R.layout.add_certificate, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.editTextName);
        EditText issuedByInput = dialogView.findViewById(R.id.editTextIssuedBy);
        EditText dateIssuedInput = dialogView.findViewById(R.id.editTextDateIssued);
        EditText scoreInput = dialogView.findViewById(R.id.editTextScore);
        EditText remarksInput = dialogView.findViewById(R.id.editTextRemarks);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String issuedBy = issuedByInput.getText().toString().trim();
            String dateIssued = dateIssuedInput.getText().toString().trim();
            String scoreStr = scoreInput.getText().toString().trim();
            String remarks = remarksInput.getText().toString().trim();

            if (name.isEmpty() || issuedBy.isEmpty() || dateIssued.isEmpty() || scoreStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double score;
            try {
                score = Double.parseDouble(scoreStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid score format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate a unique ID for the new certificate
            String uniqueId = UUID.randomUUID().toString();

            Certificate newCertificate = new Certificate(uniqueId, student.getStudentID(), name, issuedBy, dateIssued, score, remarks);
            DbQuery.addCertificate(newCertificate, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(StudentDetailActivity.this, "Certificate added successfully", Toast.LENGTH_SHORT).show();
                    loadCertificates(student.getStudentID());
                }

                @Override
                public void onFailure() {
                    Toast.makeText(StudentDetailActivity.this, "Failed to add certificate", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadCertificates(String studentID) {
        DbQuery.loadCertificates(studentID, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                setupCertificatesList(DbQuery.certificateList);
            }

            @Override
            public void onFailure() {
                Toast.makeText(StudentDetailActivity.this, "Error loading certificates", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSelectedCertificates() {
        List<Certificate> selectedCertificates = certificateAdapter.getSelectedCertificates();

        if (selectedCertificates.isEmpty()) {
            Toast.makeText(this, "No certificates selected for deletion.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Certificate certificate : selectedCertificates) {
            DbQuery.deleteCertificate(certificate.getCertificateID(), new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    loadCertificates(studentID); // Reload certificates after deletion
                    Toast.makeText(StudentDetailActivity.this, "Certificate deleted successfully.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(StudentDetailActivity.this, "Failed to delete certificate: " + certificate.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void updateSelectedCertificates() {
        List<Certificate> selectedCertificates = certificateAdapter.getSelectedCertificates();

        if (selectedCertificates.isEmpty()) {
            Toast.makeText(this, "No certificates selected for update.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Certificate certificate : selectedCertificates) {
            openUpdateCertificateDialog(certificate);
        }
    }

    private void openUpdateCertificateDialog(Certificate certificate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Certificate");

        View dialogView = getLayoutInflater().inflate(R.layout.add_certificate, null);
        builder.setView(dialogView);

        EditText nameInput = dialogView.findViewById(R.id.editTextName);
        EditText issuedByInput = dialogView.findViewById(R.id.editTextIssuedBy);
        EditText dateIssuedInput = dialogView.findViewById(R.id.editTextDateIssued);
        EditText scoreInput = dialogView.findViewById(R.id.editTextScore);
        EditText remarksInput = dialogView.findViewById(R.id.editTextRemarks);

        // Điền các trường với dữ liệu hiện tại
        nameInput.setText(certificate.getName());
        issuedByInput.setText(certificate.getIssuedBy());
        dateIssuedInput.setText(certificate.getDateIssued());
        scoreInput.setText(String.valueOf(certificate.getScore()));
        remarksInput.setText(certificate.getRemarks());

        builder.setPositiveButton("Update", (dialog, which) -> {
            // Cập nhật thông tin chứng chỉ
            certificate.setName(nameInput.getText().toString().trim());
            certificate.setIssuedBy(issuedByInput.getText().toString().trim());
            certificate.setDateIssued(dateIssuedInput.getText().toString().trim());
            double score = Double.parseDouble(scoreInput.getText().toString().trim());
            certificate.setScore(score);
            certificate.setRemarks(remarksInput.getText().toString().trim());

            // Gọi phương thức cập nhật
            DbQuery.updateCertificate(certificate, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    Log.d("DbQuery", "Certificate updated successfully");
                    Toast.makeText(StudentDetailActivity.this, "Certificate updated successfully", Toast.LENGTH_SHORT).show();
                    loadCertificates(certificate.getStudentID()); // Gọi lại để làm mới dữ liệu
                }

                @Override
                public void onFailure() {
                    Log.e("DbQuery", "Failed to update certificate");
                    Toast.makeText(StudentDetailActivity.this, "Failed to update certificate", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void setupCertificatesList(List<Certificate> certificatesList) {
        if (certificatesList != null && !certificatesList.isEmpty()) {
            certificateAdapter = new CertificateAdapter(certificatesList);
            recyclerViewCertificates.setAdapter(certificateAdapter);
            recyclerViewCertificates.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Toast.makeText(this, "No certificates found.", Toast.LENGTH_SHORT).show();
        }
    }
}