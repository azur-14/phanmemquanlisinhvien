package com.example.giuaky;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StudentManagementActivity extends AppCompatActivity {
    private RecyclerView rcvStudent;
    private Toolbar toolbar;
    private EditText searchEditText;
    private ImageButton searchButton;
    private StudentAdapter studentAdapter;
    private List<Student> students;
    private Spinner sortSpinner;
    private Button deleteButton;
    private Button addButton;

    private ActivityResultLauncher<Intent> addStudentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize student list and adapter
        students = new ArrayList<>();
        studentAdapter = new StudentAdapter(students);

        // Initialize RecyclerView
        rcvStudent = findViewById(R.id.rcvStudent);
        rcvStudent.setAdapter(studentAdapter);
        rcvStudent.setLayoutManager(new LinearLayoutManager(this));

        // Initialize search components
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchitem);

        // Initialize sort spinner and buttons
        sortSpinner = findViewById(R.id.sortbaseon);
        deleteButton = findViewById(R.id.deleteButton);
        addButton = findViewById(R.id.addButton);

        // Load students from Firestore
        loadStudents();

        addStudentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Student newStudent = (Student) data.getSerializableExtra("NEW_STUDENT");
                            if (newStudent != null) {
                                // Add student to Firestore
                                DbQuery.addStudent(newStudent, new MyCompleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        students.add(newStudent);
                                        studentAdapter.updateStudentList(new ArrayList<>(students));
                                        Toast.makeText(StudentManagementActivity.this, "Student added successfully.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure() {
                                        Toast.makeText(StudentManagementActivity.this, "Failed to add student.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(this, "No student data received.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        // Setup listener for addButton
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentManagementActivity.this, AddStudentActivity.class);
            addStudentLauncher.launch(intent); // Launch activity to add new student
        });

        // Setup Spinner for sorting options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        // Add TextWatcher for search input
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup listener for search button
        searchButton.setOnClickListener(v -> performSearch(searchEditText.getText().toString()));

        // Setup listener for delete button
        deleteButton.setOnClickListener(v -> deleteSelectedStudents());

        // Handle item selection in Spinner
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadStudents() {
        DbQuery.loadStudents(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                // Assuming userList now contains the loaded student data
                students.clear();
                students.addAll(DbQuery.userList.stream()
                        .filter(user -> user instanceof Student)
                        .map(user -> (Student) user)
                        .collect(Collectors.toList()));
                studentAdapter.updateStudentList(students);
            }

            @Override
            public void onFailure() {
                Toast.makeText(StudentManagementActivity.this, "Failed to load students.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String query) {
        List<Student> filteredStudents = students.stream()
                .filter(student -> student.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        studentAdapter.updateStudentList(filteredStudents);

        if (filteredStudents.isEmpty()) {
            Toast.makeText(this, "No students found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sortStudents() {
        String selectedOption = sortSpinner.getSelectedItem().toString();
        Comparator<Student> comparator;

        switch (selectedOption) {
            case "MSSV (Tăng dần)":
                comparator = Comparator.comparing(Student::getStudentID);
                break;
            case "MSSV (Giảm dần)":
                comparator = Comparator.comparing(Student::getStudentID).reversed();
                break;
            case "Tên (A-Z)":
                comparator = Comparator.comparing(Student::getName);
                break;
            case "Tên (Z-A)":
                comparator = Comparator.comparing(Student::getName).reversed();
                break;
            case "Khoa":
                comparator = Comparator.comparing(Student::getFaculty);
                break;
            default:
                return;
        }

        students.sort(comparator);
        studentAdapter.updateStudentList(students);
    }

    private void deleteSelectedStudents() {
        List<Student> selectedStudents = studentAdapter.getSelectedStudents();

        if (selectedStudents.isEmpty()) {
            Toast.makeText(this, "No students selected for deletion.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Loop through selected students and delete from Firestore
        for (Student student : selectedStudents) {
            DbQuery.deleteStudent(student.getStudentID(), new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    // Remove from local list after successful deletion
                    students.remove(student);
                    studentAdapter.updateStudentList(students);
                    Toast.makeText(StudentManagementActivity.this, "Selected students deleted from Firestore.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(StudentManagementActivity.this, "Failed to delete student: " + student.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Clear selections in the adapter
        studentAdapter.clearSelections();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Import) {
            // Handle import functionality
            return true;
        }
        if (id == R.id.Export) {
            // Handle export functionality
            return true;
        }
        if (id == android.R.id.home) {
            finish(); // Close current activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}