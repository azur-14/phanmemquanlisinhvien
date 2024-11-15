package com.example.giuaky;

import static com.example.giuaky.DbQuery.studentList;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private Button viewButton;

    private ActivityResultLauncher<Intent> addStudentLauncher;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    private static final int REQUEST_CODE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize student list and adapter
        students = new ArrayList<>();
        studentAdapter = new StudentAdapter(students);

        // Initialize RecyclerView
        rcvStudent = findViewById(R.id.rcvStudent);
        rcvStudent.setAdapter(studentAdapter);
        rcvStudent.setLayoutManager(new LinearLayoutManager(this));

        // Initialize UI components
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchitem);
        sortSpinner = findViewById(R.id.sortbaseon);
        deleteButton = findViewById(R.id.deleteButton);
        addButton = findViewById(R.id.addButton);
        viewButton = findViewById(R.id.viewButton);

        // Load students from Firestore
        loadStudents();

        // Register activity result launcher for adding students
        addStudentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Student newStudent = (Student) data.getSerializableExtra("NEW_STUDENT");
                            if (newStudent != null) {
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

        // Register activity result launcher for file picking
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri uri = result.getData().getData();
                        importExcelFile(uri);
                    }
                }
        );

        // Setup listeners
        addButton.setOnClickListener(v -> addNewStudent());
        viewButton.setOnClickListener(v -> viewSelectedStudents());
        searchButton.setOnClickListener(v -> performSearch(searchEditText.getText().toString()));
        deleteButton.setOnClickListener(v -> deleteSelectedStudents());

        // Setup sort spinner
        setupSortSpinner();

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
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now access the file
                // You might want to call your method to proceed with file access here
                openFilePicker(); // or handle the original URI if applicable
            } else {
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Excel file type
        filePickerLauncher.launch(intent);
    }

    private void importExcelFile(Uri uri) {
        // No need for permission check; URI is handled by the file picker
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                Toast.makeText(this, "Unable to open file", Toast.LENGTH_SHORT).show();
                return;
            }

            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from 1 to skip header row
                Row row = sheet.getRow(i);
                if (row == null) continue; // Skip empty rows

                String maSV = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "";
                String ten = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
                String khoa = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
                String nganh = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : "";
                int tuoi = row.getCell(4) != null ? (int) row.getCell(4).getNumericCellValue() : 0;
                String soDienThoai = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : "";
                String email = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : "";

                // Validate data before adding
                if (!maSV.isEmpty() && !ten.isEmpty()) {
                    Student newStudent = new Student(maSV, ten, khoa, nganh, tuoi, soDienThoai, email);
                    students.add(newStudent);
                    DbQuery.addStudent(newStudent, new MyCompleteListener() {
                        @Override
                        public void onSuccess() {
                            // Optionally handle success for each student
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(StudentManagementActivity.this, "Failed to add student: " + ten, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            Toast.makeText(this, "Data imported successfully!", Toast.LENGTH_SHORT).show();
            studentAdapter.updateStudentList(students); // Update the adapter with new data

        } catch (IOException e) {
            Toast.makeText(this, "Error reading the file. Please try again.", Toast.LENGTH_SHORT).show();
            e.printStackTrace(); // Log the exception for debugging
        }
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
            openFilePicker(); // Open the file picker
            return true;
        }
        if (id == R.id.Export) {
            createExcelFile(students);
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void addNewStudent() {
        Intent intent = new Intent(StudentManagementActivity.this, AddStudentActivity.class);
        addStudentLauncher.launch(intent);
    }

    private void loadStudents() {
        DbQuery.loadStudent(new MyCompleteListener() {
            @Override
            public void onSuccess() {
                students.clear();
                students.addAll(DbQuery.studentList);
                Log.d("LoadStudents", "Number of students loaded: " + students.size());
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

        final int[] deletionCount = {0};
        final int totalStudentsToDelete = selectedStudents.size();

        for (Student student : selectedStudents) {
            DbQuery.deleteStudent(student.getStudentID(), new MyCompleteListener() {
                @Override
                public void onSuccess() {
                    deletionCount[0]++;
                    students.remove(student);

                    if (deletionCount[0] == totalStudentsToDelete) {
                        studentAdapter.updateStudentList(students);
                        studentAdapter.clearSelections();
                        Toast.makeText(StudentManagementActivity.this, "Selected students deleted.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure() {
                    Toast.makeText(StudentManagementActivity.this, "Failed to delete student: " + student.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void createExcelFile(List<Student> students) {
        // Implementation for creating an Excel file
    }

    private void viewSelectedStudents() {
        List<Student> selectedStudents = studentAdapter.getSelectedStudents();

        if (selectedStudents.size() == 1) {
            Intent intent = new Intent(this, StudentDetailActivity.class);
            intent.putExtra("SELECTED_STUDENT", selectedStudents.get(0));
            startActivity(intent);
        } else if (selectedStudents.isEmpty()) {
            Toast.makeText(this, "Please select one student to view.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please select only one student to view.", Toast.LENGTH_SHORT).show();
        }
    }
}