package com.example.studentmanagement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.example.studentmanagement.adapter.StudentAdapter;
import com.example.studentmanagement.model.Student;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StudentManagementActivity extends AppCompatActivity implements StudentAdapter.OnUserClickListener {
    private RecyclerView rcvStudent;
    private Toolbar toolbar;
    private EditText searchEditText;
    private ImageButton searchButton;
    private StudentAdapter studentAdapter;
    private List<Student> students;
    private Spinner sortSpinner;
    private Button deleteButton;
    private Button addButton;
    private SharedPreferences sharedPref;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = firebaseFirestore.collection("Users");

    private ActivityResultLauncher<Intent> addStudentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        students = new ArrayList<>();
        initializeSampleData();
        studentAdapter = new StudentAdapter(students, this);

        rcvStudent = findViewById(R.id.rcvStudent);
        rcvStudent.setAdapter(studentAdapter);
        rcvStudent.setLayoutManager(new LinearLayoutManager(this));

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchitem);

        sortSpinner = findViewById(R.id.sortbaseon);
        deleteButton = findViewById(R.id.deleteButton);
        addButton = findViewById(R.id.addButton);

        addStudentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("NEW_STUDENT")) {
                            Student newStudent = (Student) data.getSerializableExtra("NEW_STUDENT");
                            students.add(newStudent);
                            studentAdapter.updateStudentList(students);
                            Toast.makeText(this, "Student added successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddStudentActivity.class);
            addStudentLauncher.launch(intent);
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

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

        searchButton.setOnClickListener(v -> performSearch(searchEditText.getText().toString()));

        deleteButton.setOnClickListener(v -> deleteSelectedStudents());

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initializeSampleData() {
        students.add(new Student("email@example.com", "John Doe", 20, "1234567890", false, 1, "1001", "Computer Science", "Software Engineering"));
        students.add(new Student("email2@example.com", "Jane Smith", 21, "0987654321", false, 2, "1002", "Information Technology", "Networking"));
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

        students.removeAll(selectedStudents);
        studentAdapter.updateStudentList(students);
        studentAdapter.clearSelections();
        Toast.makeText(this, "Selected students deleted.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.student_management) {
            if (!(this instanceof StudentManagementActivity)) {
                Intent intent = new Intent(this, StudentManagementActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return true;
        }

        // Handle other menu items
        String accountUID = sharedPref.getString(getString(R.string.saved_account_uid_key), "");

        if (id == R.id.create) {
            Intent intent = new Intent(StudentManagementActivity.this, CreateEditUserActivity.class);
            intent.putExtra("action", "add");
            startActivity(intent);
        } else if (id == R.id.account_management) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        } else if (id == R.id.info) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("accountUID", accountUID);
            startActivity(intent);
        } else if (id == R.id.log_out) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditUserClick(String uid) {
        Intent intent = new Intent(this, CreateEditUserActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("action", "edit");
        startActivity(intent);
    }

    @Override
    public void onDeleteUserClick(String uid) {
        usersRef.document(uid).delete();
    }

    @Override
    public void onSeeDetailedUserInfoClick(String uid) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("userUID", uid);
        startActivity(intent);
    }
}
