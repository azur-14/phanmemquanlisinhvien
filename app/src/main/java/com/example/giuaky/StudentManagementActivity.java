package com.example.giuaky;

import static com.example.giuaky.DbQuery.studentList;
import static com.example.giuaky.DbQuery.userList;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StudentManagementActivity extends AppCompatActivity {
    private RecyclerView rcvStudent;
    private Toolbar toolbar;
    private StudentAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_management);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        rcvStudent = findViewById(R.id.rcvStudent);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcvStudent.setLayoutManager(layoutManager);

        DbQuery.loadStudent(new MyCompleteListener(){
            @Override
            public void onSuccess() {
                studentAdapter = new StudentAdapter(studentList);
                rcvStudent.setAdapter(studentAdapter);
            }

            @Override
            public void onFailure() {
                Toast.makeText(StudentManagementActivity.this,"Đã xảy ra lỗi khi load dữ liệu người đùng ! Vui lòng thử lại sau",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.Add){
            Toast.makeText(this,"Thêm Sinh Viên", Toast.LENGTH_SHORT).show();
        }
        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return true;
    }
}