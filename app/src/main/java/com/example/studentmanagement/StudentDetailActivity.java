package com.example.studentmanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentDetailActivity extends AppCompatActivity {
    final static String STUDENTS = "Students";
    TextView tvName;
    EditText etAge;
    EditText etPhoneNumber;
    EditText etGender;
    EditText etEmail;
    EditText etGpa;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String studentUID;
    SharedPreferences sharedPref;
    int role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        tvName = findViewById(R.id.tv_name);
        etAge = findViewById(R.id.et_age);
        etPhoneNumber = findViewById(R.id.et_phoneNumber);
        etGender = findViewById(R.id.et_gender);
        etEmail = findViewById(R.id.et_email);
        etGpa = findViewById(R.id.et_gpa);

        // Get role and update UI based on role
        this.sharedPref = getSharedPreferences(getString(R.string.preference_login_key), Context.MODE_PRIVATE);
        role = sharedPref.getInt(getString(R.string.saved_role_key), 0);

        Intent intent = getIntent();
        studentUID = intent.getStringExtra("studentUID");
        firebaseFirestore.collection(STUDENTS).document(studentUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                etEmail.setText((String) documentSnapshot.get("email"));
                tvName.setText((String) documentSnapshot.get("name"));
                etAge.setText(String.valueOf((long) documentSnapshot.get("age")));
                etPhoneNumber.setText((String) documentSnapshot.get("phoneNumber"));
                etGpa.setText(String.valueOf(documentSnapshot.get("gpa")));
                boolean isMale = (Boolean) documentSnapshot.get("male");
                if (isMale) {
                    etGender.setText("Male");
                } else {
                    etGender.setText("Female");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_management, menu);
        if (role == 0 || role == 1) {
            MenuItem itemAccountManagement = menu.findItem(R.id.account_management);
            itemAccountManagement.setVisible(false);
        }
        if (role == 0) {
            MenuItem itemAdd = menu.findItem(R.id.create);
            itemAdd.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String accountUID = sharedPref.getString(getString(R.string.saved_account_uid_key), "");
        if (item.getItemId() == R.id.create) {
            Intent intent = new Intent(StudentDetailActivity.this, CreateEditUserActivity.class);
            intent.putExtra("action", "add");
            startActivity(intent);
        } else if (item.getItemId() == R.id.account_management) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.student_management) {
            Intent intent = new Intent(this, StudentManagementActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.info) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("accountUID", accountUID);
            startActivity(intent);
        } else if (item.getItemId() == R.id.log_out) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
