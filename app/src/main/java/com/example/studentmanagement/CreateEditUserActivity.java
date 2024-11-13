package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateEditUserActivity extends AppCompatActivity {

    private TextView tvTitle;
    private EditText etEmail, etName, etAge, etPhoneNumber;
    private RadioGroup rgRole;
    private RadioButton rbEmployee, rbManager;
    private Button btnSave;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_user);

        initializeViews();

        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (intent != null && "edit".equals(intent.getStringExtra("action"))) {
            etEmail.setVisibility(View.GONE);
            loadUserData(intent.getStringExtra("uid"));
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSaveUser(intent);
            }
        });
    }

    private void initializeViews() {
        tvTitle = findViewById(R.id.tv_title);
        etEmail = findViewById(R.id.et_email);
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etPhoneNumber = findViewById(R.id.et_phoneNumber);
        rgRole = findViewById(R.id.rg_role);
        rbEmployee = findViewById(R.id.rb_employee);
        rbManager = findViewById(R.id.rb_manager);
        btnSave = findViewById(R.id.btn_save);
    }

    private void loadUserData(String uid) {
        firebaseFirestore.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    tvTitle.setText("  Edit User");
                    btnSave.setText("Save");

                    etName.setText(documentSnapshot.getString("name"));
                    etAge.setText(String.valueOf(documentSnapshot.getLong("age")));
                    etPhoneNumber.setText(documentSnapshot.getString("phoneNumber"));

                    int role = documentSnapshot.getLong("role").intValue();
                    if (role == 0) rbEmployee.setChecked(true);
                    else if (role == 1) rbManager.setChecked(true);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(CreateEditUserActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show());
    }

    private void handleSaveUser(Intent intent) {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if ((intent == null || !"edit".equals(intent.getStringExtra("action"))) && email.isEmpty()) {
            etEmail.setError("Email is required.");
            return;
        }

        if (name.isEmpty() || ageStr.isEmpty() || phoneNumber.isEmpty() || rgRole.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        int role = rgRole.getCheckedRadioButtonId() == R.id.rb_employee ? 0 :
                rgRole.getCheckedRadioButtonId() == R.id.rb_manager ? 1 : 2;

        if (intent != null && "edit".equals(intent.getStringExtra("action"))) {
            String uid = intent.getStringExtra("uid");
            User user = new User(uid, name, age, phoneNumber, role);
            updateUser(uid, user);
        } else {
            String uid = firebaseFirestore.collection("Users").document().getId(); // Generate unique ID for user
            User user = new User(uid, name, age, phoneNumber, role);
            saveNewUser(uid, user);
        }
    }

    private void updateUser(String uid, User user) {
        firebaseFirestore.collection("Users").document(uid).set(user)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "User updated successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update user.", Toast.LENGTH_SHORT).show());
    }

    private void saveNewUser(String uid, User user) {
        firebaseFirestore.collection("Users").document(uid).set(user)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "User created successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show());
    }
}
