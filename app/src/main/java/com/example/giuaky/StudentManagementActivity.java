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

        // Khởi tạo toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Khởi tạo danh sách sinh viên và adapter
        students = new ArrayList<>();
        initializeSampleData();
        studentAdapter = new StudentAdapter(students);

        // Khởi tạo RecyclerView
        rcvStudent = findViewById(R.id.rcvStudent);
        rcvStudent.setAdapter(studentAdapter);
        rcvStudent.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo các thành phần tìm kiếm
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchitem);

        // Khởi tạo các thành phần sắp xếp
        sortSpinner = findViewById(R.id.sortbaseon);
        deleteButton = findViewById(R.id.deleteButton);
        addButton = findViewById(R.id.addButton);

        addStudentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Student newStudent = (Student) data.getSerializableExtra("NEW_STUDENT");
                            if (newStudent != null) {
                                students.add(newStudent); // Thêm sinh viên vào danh sách
                                studentAdapter.updateStudentList(new ArrayList<>(students)); // Cập nhật RecyclerView
                                Toast.makeText(this, "Student added successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "No student data received.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        // Thiết lập listener cho addButton
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentManagementActivity.this, AddStudentActivity.class);
            addStudentLauncher.launch(intent); // Sử dụng ActivityResultLauncher
        });

        // Thiết lập Spinner cho các tùy chọn sắp xếp
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        // Thêm TextWatcher cho input tìm kiếm
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

        // Thiết lập listener cho nút tìm kiếm
        searchButton.setOnClickListener(v -> performSearch(searchEditText.getText().toString()));

        // Thiết lập listener cho nút xóa
        deleteButton.setOnClickListener(v -> deleteSelectedStudents());

        // Xử lý sự kiện chọn mục trong Spinner
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
        students.add(new Student("student", "Lê Ngọc Mạnh Hùng", "lengocmanhhung@student.tdtu.edu.vn", "0987654321", "normal", "50001", 23, "Công nghệ thông tin", "Kĩ thuật phần mềm"));
        students.add(new Student("student", "Trần Gia Mẫn", "trangiaman@student.tdtu.edu.vn", "0984425421", "normal", "50002", 23, "Công nghệ thông tin", "Kĩ thuật phần mềm"));
        // Thêm dữ liệu mẫu khác nếu cần
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
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Import) {
            ///
            return true;
        }
        if (id == R.id.Export) {
            ///
            return true;
        }
        if (id == android.R.id.home) {
            finish(); // Đóng Activity hiện tại
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}