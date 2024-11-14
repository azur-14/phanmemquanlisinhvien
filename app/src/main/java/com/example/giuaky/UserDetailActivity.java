package com.example.giuaky;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Base64;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity {
    CircleImageView civAvatar;
    TextView tvName;
    EditText etAge;
    EditText etPhoneNumber;
    EditText etRole;
    RecyclerView rv;
    LoginHistoryAdapter adapter;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Initialize views
        rv = findViewById(R.id.rv_login_history);
        tvName = findViewById(R.id.tv_name);
        etAge = findViewById(R.id.et_age);
        etPhoneNumber = findViewById(R.id.et_phoneNumber);
        etRole = findViewById(R.id.et_role);
        civAvatar = findViewById(R.id.civ_avatar);

        // Get user UID from intent
        Intent intent = getIntent();
        String userUID = intent.getStringExtra("userUID");

        // Fetch user data from Firestore
        firestore.collection("Users").document(userUID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Set user details
                        tvName.setText(documentSnapshot.getString("name"));
                        etAge.setText(String.valueOf(((Long) documentSnapshot.get("age")).intValue()));
                        etPhoneNumber.setText(documentSnapshot.getString("phoneNumber"));

                        int role = ((Long) documentSnapshot.get("role")).intValue();
                        if (role == 0) {
                            etRole.setText("Employee");
                        } else if (role == 1) {
                            etRole.setText("Manager");
                        } else if (role == 2) {
                            etRole.setText("Admin");
                        }

                        // Load avatar image
                        String base64Avatar = documentSnapshot.getString("avatar");
                        if (base64Avatar != null) {
                            byte[] binaryData = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                binaryData = Base64.getDecoder().decode(base64Avatar);
                            }
                            Bitmap bitmap = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                            civAvatar.setImageBitmap(bitmap);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

        // Set layout manager for RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Set up Firestore query for login history
        Query query = firestore.collection("Users").document(userUID).collection("LoginHistory");
        FirestoreRecyclerOptions<LoginHistory> options = new FirestoreRecyclerOptions.Builder<LoginHistory>()
                .setQuery(query, LoginHistory.class)
                .build();

        // Initialize adapter
        adapter = new LoginHistoryAdapter(options);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening(); // Start listening for updates
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening(); // Stop listening when activity is destroyed
    }
}