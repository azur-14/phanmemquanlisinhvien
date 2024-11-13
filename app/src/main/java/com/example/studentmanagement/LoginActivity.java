package com.example.studentmanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentmanagement.model.LoginHistory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {
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
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Email can not be empty", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Password can not be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        LoginActivity.this.sharedPref = getSharedPreferences(getString(R.string.preference_login_key), Context.MODE_PRIVATE);
                        LoginActivity.this.editor = sharedPref.edit();
                        firestore.collection("Users").document(authResult.getUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                // save login history
                                LoginHistory loginHistory = new LoginHistory();
                                loginHistory.setLoginDate(new Date());
                                loginHistory.setLocation("VietNam");
                                firestore.collection("Users").document(authResult.getUser().getUid()).collection("LoginHistory").add(loginHistory).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        loginHistory.setId(documentReference.getId());
                                        documentReference.set(loginHistory);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });

                                // save login information
                                int role = ((Long) documentSnapshot.get("role")).intValue();
                                LoginActivity.this.editor.putString(getString(R.string.saved_account_uid_key), authResult.getUser().getUid());
                                LoginActivity.this.editor.putInt(getString(R.string.saved_role_key), role);
                                LoginActivity.this.editor.apply();
                                if (role == 0 || role == 1) {
                                    Intent intent = new Intent(LoginActivity.this, StudentManagementActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}