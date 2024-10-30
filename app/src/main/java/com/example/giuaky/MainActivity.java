package com.example.giuaky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity { // Kế thừa từ AppCompatActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Tìm nút LOGIN và thiết lập sự kiện click
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            // Lấy thông tin từ EditText
            EditText emailEditText = findViewById(R.id.emailEditText);
            EditText passwordEditText = findViewById(R.id.passwordEditText);

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            // Kiểm tra thông tin đăng nhập (có thể thay bằng logic thực tế)
            if (validateCredentials(email, password)) {
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent); // Khởi động Home activity
            } else {
                // Hiển thị thông báo lỗi (có thể sử dụng Toast hoặc AlertDialog)
                emailEditText.setError("Invalid email or password");
            }
        });
    }

    // Phương thức kiểm tra thông tin đăng nhập (có thể thay thế bằng logic thực tế)
    private boolean validateCredentials(String email, String password) {
        return email.equals("test@example.com") && password.equals("password123");
    }
}