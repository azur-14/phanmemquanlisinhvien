package com.example.giuaky;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_management);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        List<Student> students = new ArrayList<>();
        students.add(new Student("student","Lê Ngọc Mạnh Hùng","lengocmanhhung@student.tdtu.edu.vn","0987654321","normal","SV0001",23, "Công nghệ thông tin","Kĩ thuật phần mềm"));
        students.add(new Student("student","Trần Gia Mẫn","trangiaman@student.tdtu.edu.vn","0984425421","normal","SV0002",23, "Công nghệ thông tin","Kĩ thuật phần mềm"));

        rcvStudent = findViewById(R.id.rcvStudent);
        StudentAdapter adapter = new StudentAdapter(students);
        rcvStudent.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcvStudent.setLayoutManager(layoutManager);
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