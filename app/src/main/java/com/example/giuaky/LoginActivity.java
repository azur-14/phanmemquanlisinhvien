package com.example.giuaky;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giuaky.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim(); // Xóa khoảng trắng
                String password = etPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Email can not be empty", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Password can not be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d(TAG, "Attempting to sign in with email: " + email);

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.d(TAG, "Sign in successful for user: " + authResult.getUser().getUid());
                                sharedPref = getSharedPreferences(getString(R.string.preference_login_key), Context.MODE_PRIVATE);
                                editor = sharedPref.edit();

                                // Truy vấn tài liệu theo thuộc tính 'id'
                                firestore.collection("Users")
                                        .whereEqualTo("id", email) // Truy vấn theo thuộc tính 'id'
                                        .get()
                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                                Log.d(TAG, "User document retrieved: " + documentSnapshot.getId());

                                                // Lưu lịch sử đăng nhập
                                                LoginHistory loginHistory = new LoginHistory();
                                                loginHistory.setLoginDate(new Date());
                                                loginHistory.setLocation("VietNam");

                                                firestore.collection("Users").document(documentSnapshot.getId())
                                                        .collection("LoginHistory").add(loginHistory)
                                                        .addOnSuccessListener(documentReference -> {
                                                            loginHistory.setId(documentReference.getId());
                                                            Log.d(TAG, "Login history saved with ID: " + documentReference.getId());
                                                        }).addOnFailureListener(e -> {
                                                            Log.e(TAG, "Failed to save login history: " + e.getMessage());
                                                            Toast.makeText(LoginActivity.this, "Error saving login history", Toast.LENGTH_SHORT).show();
                                                        });

                                                // Lưu thông tin đăng nhập
                                                Long roleLong = documentSnapshot.getLong("role");
                                                if (roleLong != null) {
                                                    int role = roleLong.intValue();
                                                    editor.putString(getString(R.string.saved_account_uid_key), email);
                                                    editor.putInt(getString(R.string.saved_role_key), role);
                                                    editor.apply();
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Log.e(TAG, "Role not found in document");
                                                    Toast.makeText(LoginActivity.this, "Role not found", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                Log.e(TAG, "User document does not exist");
                                                Toast.makeText(LoginActivity.this, "User document does not exist", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(e -> {
                                            Log.e(TAG, "Error getting user document: " + e.getMessage());
                                            Toast.makeText(LoginActivity.this, "Error retrieving user information", Toast.LENGTH_LONG).show();
                                        });
                            }
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Login failed: " + e.getMessage());
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });
    }
}