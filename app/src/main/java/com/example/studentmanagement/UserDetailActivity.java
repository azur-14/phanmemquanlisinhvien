package com.example.studentmanagement;

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

import com.example.studentmanagement.adapter.LoginHistoryAdapter;
import com.example.studentmanagement.model.LoginHistory;
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

        rv = findViewById(R.id.rv_login_history);
        tvName = findViewById(R.id.tv_name);
        etAge = findViewById(R.id.et_age);
        etPhoneNumber = findViewById(R.id.et_phoneNumber);
        etRole = findViewById(R.id.et_role);
        civAvatar = findViewById(R.id.civ_avatar);

        Intent intent = getIntent();
        String userUID = intent.getStringExtra("userUID");

        firestore.collection("Users").document(userUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tvName.setText((String) documentSnapshot.get("name"));
                etAge.setText(String.valueOf(((Long) documentSnapshot.get("age")).intValue()));
                etPhoneNumber.setText((String) documentSnapshot.get("phoneNumber"));
                int role = ((Long) documentSnapshot.get("role")).intValue();
                if (role == 0) {
                    etRole.setText("Teacher");
                } else if (role == 1) {
                    etRole.setText("Student");
                } else if (role == 2) {
                    etRole.setText("Admin");
                }

                String base64Avatar = (String) documentSnapshot.get("avatar");
                if (base64Avatar != null) {
                    byte[] binaryData = Base64.getDecoder().decode(base64Avatar);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                    civAvatar.setImageBitmap(bitmap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        // set layout manager
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // set adapter
        Query query = firestore.collection("Users").document(userUID).collection("LoginHistory");

        // simply get the data from query to the adapter
        FirestoreRecyclerOptions<LoginHistory> options = new FirestoreRecyclerOptions.Builder<LoginHistory>().setQuery(query, LoginHistory.class).build();
        adapter = new LoginHistoryAdapter(options);
        rv.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }
}