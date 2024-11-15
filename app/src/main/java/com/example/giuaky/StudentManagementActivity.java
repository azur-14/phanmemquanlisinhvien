package com.example.giuaky;
import static com.example.giuaky.DbQuery.studentList;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import android.os.Environment;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    private Button viewButton;

    private ActivityResultLauncher<Intent> addStudentLauncher;

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
    public void importExcelFile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {

            // Path to the file in the Downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(downloadsDir, "ThemSinhVien.xlsx");
            if (!file.exists()) {
                Toast.makeText(this, "File không tồn tại trong thư mục Download", Toast.LENGTH_SHORT).show();
                return;
            }

            FileInputStream inputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue; // Skip empty rows

                // Read data from columns and check for empty values
                String maSV = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "";
                String ten = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
                String khoa = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
                String nganh = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : "";
                int tuoi = row.getCell(4) != null ? (int) row.getCell(4).getNumericCellValue() : 0;
                String soDienThoai = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : "";
                String email = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : "";

                // Validate data before adding
                if (!maSV.isEmpty() && !ten.isEmpty()) {
                    students.add(new Student(maSV, ten, khoa, nganh, tuoi, soDienThoai, email));
                }
            }

            // Notify success
            Toast.makeText(this, "Nhập dữ liệu thành công!", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Đã xảy ra lỗi khi đọc file", Toast.LENGTH_SHORT).show();
        }
    }



    public void createExcelFile(List<Student> students) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Student Data");

        // Create the header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Mã SV", "Tên", "Email", "Số điện thoại", "Tuổi", "Khoa", "Ngành"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Add data to each row
        for (int i = 0; i < students.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Student student = students.get(i);

            row.createCell(0).setCellValue(student.getStudentID());
            row.createCell(1).setCellValue(student.getName());
            row.createCell(2).setCellValue(student.getEmail());
            row.createCell(3).setCellValue(student.getPhoneNumber());
            row.createCell(4).setCellValue(student.getAge());
            row.createCell(5).setCellValue(student.getFaculty());
            row.createCell(6).setCellValue(student.getMajor());
        }

        // Save the Excel file in the Downloads directory
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, "StudentData.xlsx");

            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();

            Toast.makeText(this, "File danh sách sinh viên đã được lưu trong thư mục Downloads", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Đã xảy ra lỗi khi lưu file Excel", Toast.LENGTH_LONG).show();
        }
    }

    private void viewSelectedStudents() {
        List<Student> selectedStudents = studentAdapter.getSelectedStudents();

        if (selectedStudents.size() == 1) {
            // Proceed if exactly one student is selected
            Intent intent = new Intent(this, StudentDetailActivity.class);
            intent.putExtra("SELECTED_STUDENT", selectedStudents.get(0)); // Pass the single selected student
            startActivity(intent);
        } else if (selectedStudents.isEmpty()) {
            // Notify the user if no student is selected
            Toast.makeText(this, "Please select one student to view.", Toast.LENGTH_SHORT).show();
        } else {
            // Notify the user if more than one student is selected
            Toast.makeText(this, "Please select only one student to view.", Toast.LENGTH_SHORT).show();
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
            // Handle import functionality
            return true;
        }
        if (id == R.id.Export) {
            createExcelFile(students);
            Toast.makeText(this, "File danh sách sinh viên đã được lưu trong thư mục Downloads", Toast.LENGTH_LONG).show();
            importExcelFile();
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
