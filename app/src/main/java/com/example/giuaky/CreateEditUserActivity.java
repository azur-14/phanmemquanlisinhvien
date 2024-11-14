package com.example.giuaky;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class CreateEditUserActivity extends AppCompatActivity {

    private EditText etEmail, etName, etAge, etPhoneNumber;
    private RadioGroup rgRole, rgStatus;
    private Button btnSave, btnSelectImage;
    private ImageView imageView;

    private FirebaseFirestore firebaseFirestore;
    private Bitmap selectedImageBitmap;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    loadImage(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_user);

        initializeViews();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (intent != null && "edit".equals(intent.getStringExtra("action"))) {
            etEmail.setVisibility(View.GONE); // Hide email field when editing
            String uid = intent.getStringExtra("uid");
            if (uid != null) {
                loadUserData(uid); // Load user data if UID is provided
            } else {
                showToast("User ID is missing.");
                finish(); // Close the activity if UID is not available
            }
        } else {
            setCreateModeHints(); // Set hints for creating a new user
        }

        btnSelectImage.setOnClickListener(view -> openImagePicker());
        btnSave.setOnClickListener(view -> handleSaveUser(intent));
    }

    private void initializeViews() {
        imageView = findViewById(R.id.imageView);
        etEmail = findViewById(R.id.et_email);
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etPhoneNumber = findViewById(R.id.et_phoneNumber);
        rgRole = findViewById(R.id.rg_role);
        rgStatus = findViewById(R.id.rg_status);
        btnSave = findViewById(R.id.btn_save);
        btnSelectImage = findViewById(R.id.btn_selectimage);
    }

    private void setCreateModeHints() {
        etEmail.setHint("Enter email");
        etName.setHint("Enter name");
        etAge.setHint("Enter age");
        etPhoneNumber.setHint("Enter phone number");
        rgRole.clearCheck(); // Clear any selected role
        rgStatus.clearCheck(); // Clear any selected status
    }

    private void loadUserData(String uid) {
        firebaseFirestore.collection("Users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        populateUserData(documentSnapshot);
                    } else {
                        showToast("User not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error loading user data", e);
                    showToast("Failed to load user data.");
                });
    }

    private void populateUserData(DocumentSnapshot documentSnapshot) {
        try {
            etName.setText(documentSnapshot.getString("name"));
            etEmail.setText(documentSnapshot.getString("email"));
            etAge.setText(String.valueOf(documentSnapshot.getLong("age")));
            etPhoneNumber.setText(documentSnapshot.getString("phoneNumber"));
            rgRole.check(documentSnapshot.getLong("role") == 0 ? R.id.rb_employee : R.id.rb_manager);
            if (documentSnapshot.getBoolean("isLocked")) {
                rgStatus.check(R.id.rb_inactive);
            }

            String imageBase64 = documentSnapshot.getString("imageBase64");
            if (imageBase64 != null) {
                selectedImageBitmap = decodeBase64ToBitmap(imageBase64);
                imageView.setImageBitmap(selectedImageBitmap);
            }
        } catch (Exception e) {
            Log.e("LoadUserData", "Error parsing user data", e);
            showToast("Error loading user data.");
        }
    }

    private Bitmap decodeBase64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadImage(Uri imageUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(imageUri)) {
            selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(selectedImageBitmap);
        } catch (Exception e) {
            Log.e("LoadImageError", "Failed to load image", e);
            showToast("Failed to load image.");
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void handleSaveUser(Intent intent) {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if ("create".equals(intent.getStringExtra("action")) && email.isEmpty()) {
            etEmail.setError("Email is required.");
            return;
        }

        if (name.isEmpty() || ageStr.isEmpty() || phoneNumber.isEmpty() || rgRole.getCheckedRadioButtonId() == -1) {
            showToast("Please fill all fields.");
            return;
        }

        int age = parseAge(ageStr);
        if (age == -1) return; // Invalid age

        int role = getRoleFromRadioGroup();
        if (role == -1) {
            showToast("Please select a role.");
            return;
        }

        boolean isLocked = rgStatus.getCheckedRadioButtonId() == R.id.rb_inactive;

        String imageBase64 = selectedImageBitmap != null ? encodeImageToBase64(selectedImageBitmap) : null;

        if ("edit".equals(intent.getStringExtra("action"))) {
            String uid = intent.getStringExtra("uid");
            updateUser(uid, new User(uid, name, age, phoneNumber, isLocked, role, imageBase64));
        } else {
            String uid = firebaseFirestore.collection("Users").document().getId();
            saveNewUser(uid, new User(uid, name, age, phoneNumber, isLocked, role, imageBase64));
        }
    }

    private int parseAge(String ageStr) {
        try {
            return Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            etAge.setError("Invalid age.");
            return -1; // Indicate invalid age
        }
    }

    private int getRoleFromRadioGroup() {
        int checkedId = rgRole.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_employee) return 0;
        if (checkedId == R.id.rb_manager) return 1;
        return -1; // Invalid role
    }

    private void updateUser(String uid, User user) {
        firebaseFirestore.collection("Users").document(uid).set(user)
                .addOnSuccessListener(unused -> {
                    showToast("User updated successfully.");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("UpdateUserError", "Failed to update user", e);
                    showToast("Failed to update user.");
                });
    }

    private void saveNewUser(String uid, User user) {
        firebaseFirestore.collection("Users").document(uid).set(user)
                .addOnSuccessListener(unused -> {
                    showToast("User created successfully.");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("SaveUserError", "Failed to save user data", e);
                    showToast("Failed to save user data.");
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}