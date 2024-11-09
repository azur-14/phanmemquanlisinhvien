package com.example.giuaky;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class StudentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private ArrayList<Student> studentList;
    private FirebaseFirestore db;
    private EditText searchEditText;
    private Spinner sortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewstudent);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        Button addStudentButton = findViewById(R.id.addStudent);
        Button deleteStudentButton = findViewById(R.id.deleteStudent);
        Button viewButton = findViewById(R.id.viewButton);
        sortSpinner = findViewById(R.id.sortbaseon); // Assuming sorting options are set up

        // Initialize the student list and adapter
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(studentAdapter);

        // Load students from Firestore
        loadStudents();

        // Set up click listeners
        addStudentButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentListActivity.this, AddStudentActivity.class);
            startActivity(intent);
        });

        // Implement delete functionality (if needed)
        deleteStudentButton.setOnClickListener(v -> {
            // Handle delete action
            // You can implement logic to delete selected students
        });

        // Implement view functionality (if needed)
        viewButton.setOnClickListener(v -> {
            // Handle view action, e.g., view selected student details
        });

        // Set up search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadStudents() {
        db.collection("students")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        studentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Student student = document.toObject(Student.class);
                            studentList.add(student);
                        }
                        studentAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void filterStudents(String query) {
        ArrayList<Student> filteredList = new ArrayList<>();
        for (Student student : studentList) {
            // Filter based on name or student ID
            if (student.getName().toLowerCase().contains(query.toLowerCase()) ||
                    student.getStudentId().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(student);
            }
        }
        studentAdapter.updateList(filteredList);
    }
}