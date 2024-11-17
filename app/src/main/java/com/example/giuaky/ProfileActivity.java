package com.example.giuaky;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64; // Sử dụng kiểu import của Android
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE_REQUEST = 1;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private TextView tvName;
    private EditText etAge, etPhoneNumber, etRole;
    private Button btnUpload;
    private ImageView ivCamera;
    private CircleImageView civAvatarEdit;
    private String accountUID;
    private Uri imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        ivCamera = findViewById(R.id.iv_camera);
        btnUpload = findViewById(R.id.btn_upload);
        civAvatarEdit = findViewById(R.id.civ_avatar_edit);
        tvName = findViewById(R.id.tv_name);
        etAge = findViewById(R.id.et_age);
        etPhoneNumber = findViewById(R.id.et_phoneNumber);
        etRole = findViewById(R.id.et_role);

        // Retrieve account UID
        accountUID = getIntent().getStringExtra("accountUID");

        if (accountUID != null) {
            loadUserData(accountUID);
        }

        ivCamera.setOnClickListener(v -> chooseImage());
        btnUpload.setOnClickListener(v -> {
            try {
                updateAvatarOfUser();
            } catch (IOException e) {
                Toast.makeText(ProfileActivity.this, "Error updating avatar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData(String userId) {
        firestore.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        tvName.setText(documentSnapshot.getString("name"));
                        etAge.setText(String.valueOf(documentSnapshot.getLong("age").intValue()));
                        etPhoneNumber.setText(documentSnapshot.getString("phoneNumber"));

                        int role = documentSnapshot.getLong("role").intValue();
                        etRole.setText(getRoleText(role));

                        String base64Avatar = documentSnapshot.getString("avatar");
                        if (base64Avatar != null) {
                            byte[] binaryData = Base64.decode(base64Avatar, Base64.DEFAULT); // Sử dụng Base64 của Android
                            Bitmap bitmap = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                            civAvatarEdit.setImageBitmap(bitmap);
                        }
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private String getRoleText(int role) {
        switch (role) {
            case 0: return "Employee";
            case 1: return "Manager";
            case 2: return "Admin";
            default: return "Unknown";
        }
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imgPath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath);
                civAvatarEdit.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void updateAvatarOfUser() throws IOException {
        if (imgPath != null) {
            InputStream inputStream = getContentResolver().openInputStream(imgPath);
            byte[] avatarBytes = getBytes(inputStream);

            // Chuyển đổi mảng byte thành Base64
            String base64Avatar = Base64.encodeToString(avatarBytes, Base64.DEFAULT);

            Map<String, Object> data = new HashMap<>();
            data.put("avatar", base64Avatar);

            firestore.collection("Users").document(accountUID).update(data)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Avatar updated successfully", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update avatar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ProfileActivity", "Error updating avatar", e);
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}