package com.example.giuaky;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class StudentDetailActivity extends AppCompatActivity {
    private TextView studentName, studentId, studentEmail, studentPhone, studentAge, studentFaculty, studentMajor;
    private ImageView studentImage;
    private RecyclerView recyclerViewCertificates;
    private CertificateAdapter certificateAdapter;
    private Toolbar toolbar;

    private Button addButton, updateButton, deleteButton;
    private String studentID;
    private String base64Image; // Store the Base64 image string
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST_DIALOG = 2; // Mã yêu cầu cho việc chọn hình ảnh trong dialog
    private ImageView currentDialogImageView; // Biến toàn cục để lưu trữ ImageView trong dialog
    // Declare student variable
    private String newBase64Image; // Biến để lưu trữ hình ảnh mới
    private Student student; // Add this line

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
        studentImage = findViewById(R.id.student_image); // Assuming you have this ImageView in your layout

        // Retrieve student data
        student = (Student) getIntent().getSerializableExtra("SELECTED_STUDENT"); // Update this line
        if (student != null) {
            populateStudentDetails(student);
            studentID = student.getStudentID();
            loadCertificates(studentID);
        } else {
            Log.e("StudentDetailActivity", "No student found in intent");
        }

        // Set up button listeners
        Button editButton = findViewById(R.id.edithocsinhbutton);
        editButton.setOnClickListener(v -> {
            if (student != null) {
                openEditStudentDialog(student);
            } else {
                Toast.makeText(this, "Student data not found", Toast.LENGTH_SHORT).show();
            }
        });

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



    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float aspectRatio = (float) width / height;

        if (width > height) {
            width = maxWidth;
            height = Math.round(maxWidth / aspectRatio);
        } else {
            height = maxHeight;
            width = Math.round(maxHeight * aspectRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    // Update your onActivityResult to resize the bitmap
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_DIALOG && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                bitmap = resizeBitmap(bitmap, 800, 800); // Thay đổi kích thước hình ảnh
                newBase64Image = convertBitmapToBase64(bitmap); // Lưu hình ảnh mới vào biến tạm thời
                currentDialogImageView.setImageBitmap(bitmap); // Cập nhật hình ảnh trong ImageView của dialog
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void chooseImageForDialog() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_DIALOG);
    }


    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress the image to reduce size (change quality as needed)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream); // Use JPEG for better compression
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
        currentDialogImageView = dialogView.findViewById(R.id.imageView); // Gán ImageView vào biến toàn cục

        // Tải dữ liệu sinh viên hiện tại vào dialog
        nameInput.setText(student.getName());
        emailInput.setText(student.getEmail());
        phoneInput.setText(student.getPhoneNumber());
        ageInput.setText(String.valueOf(student.getAge()));
        facultyInput.setText(student.getFaculty());
        majorInput.setText(student.getMajor());

        // Tải hình ảnh hiện tại
        Bitmap currentBitmap = convertIntoImage(student.getImage());
        if (currentBitmap != null) {
            currentDialogImageView.setImageBitmap(currentBitmap);
        }

        // Thêm listener để chọn hình ảnh mới
        currentDialogImageView.setOnClickListener(v -> chooseImageForDialog());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedName = nameInput.getText().toString().trim();
            String updatedEmail = emailInput.getText().toString().trim();
            String updatedPhone = phoneInput.getText().toString().trim();
            String updatedAgeStr = ageInput.getText().toString().trim();
            String updatedFaculty = facultyInput.getText().toString().trim();
            String updatedMajor = majorInput.getText().toString().trim();

            // Validate inputs
            if (updatedName.isEmpty() || updatedEmail.isEmpty() || updatedPhone.isEmpty() || updatedAgeStr.isEmpty() || updatedFaculty.isEmpty() || updatedMajor.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update student details
            student.setName(updatedName);
            student.setEmail(updatedEmail);
            student.setPhonenumber(updatedPhone);
            student.setAge(Integer.parseInt(updatedAgeStr));
            student.setFaculty(updatedFaculty);
            student.setMajor(updatedMajor);

            // Update the image with the new one
            if (newBase64Image != null) { // Ensure the new image is not null
                student.setImage(newBase64Image);
            }

            // Call the update method
            DbQuery.updateStudent(student, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(StudentDetailActivity.this, "Student details updated successfully", Toast.LENGTH_SHORT).show();
                    populateStudentDetails(student); // Refresh the displayed details
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

    public Bitmap convertIntoImage(String image) {
        if (image != null) {
            byte[] binaryData = Base64.decode(image, Base64.DEFAULT);
            // Reduce image size if needed
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; // Reduce size by half
            return BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length, options);
        }
        return null; // Return null if there is no image
    }

    private void populateStudentDetails(Student student) {
        studentName.setText(student.getName());
        studentId.setText("ID: " + student.getStudentID());
        studentEmail.setText("Email: " + student.getEmail());
        studentPhone.setText("Phone: " + student.getPhoneNumber());
        studentAge.setText("Age: " + student.getAge());
        studentFaculty.setText("Faculty: " + student.getFaculty());
        studentMajor.setText("Major: " + student.getMajor());
        try {
            Bitmap imageBitmap = convertIntoImage(student.getImage());
            if (imageBitmap != null) {
                studentImage.setImageBitmap(imageBitmap);
            } else {
                studentImage.setImageResource(R.drawable.student_image); // Default image
            }
        } catch (Exception e) {
            Log.e("StudentDetailActivity", "Error setting image: " + e.getMessage());
        }
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

        // Fill fields with current data
        nameInput.setText(certificate.getName());
        issuedByInput.setText(certificate.getIssuedBy());
        dateIssuedInput.setText(certificate.getDateIssued());
        scoreInput.setText(String.valueOf(certificate.getScore()));
        remarksInput.setText(certificate.getRemarks());

        builder.setPositiveButton("Update", (dialog, which) -> {
            // Update certificate information
            certificate.setName(nameInput.getText().toString().trim());
            certificate.setIssuedBy(issuedByInput.getText().toString().trim());
            certificate.setDateIssued(dateIssuedInput.getText().toString().trim());
            double score = Double.parseDouble(scoreInput.getText().toString().trim());
            certificate.setScore(score);
            certificate.setRemarks(remarksInput.getText().toString().trim());

            // Call the update method
            DbQuery.updateCertificate(certificate, new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    Log.d("DbQuery", "Certificate updated successfully");
                    Toast.makeText(StudentDetailActivity.this, "Certificate updated successfully", Toast.LENGTH_SHORT).show();
                    loadCertificates(certificate.getStudentID()); // Reload to refresh data
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