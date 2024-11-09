package com.example.giuaky;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {

    private EditText nameEditText, studentIdEditText, numberEditText;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addstudent);

        // Initialize the views
        nameEditText = findViewById(R.id.nameEditText);
        studentIdEditText = findViewById(R.id.studentIdEditText);
        numberEditText = findViewById(R.id.numberEditText);
        addButton = findViewById(R.id.addButton);

        // Set the button click listener
        addButton.setOnClickListener(v -> {
            // Get the input data from the EditText fields
            String name = nameEditText.getText().toString();
            String studentId = studentIdEditText.getText().toString();
            String number = numberEditText.getText().toString();

            // Create a new Intent to send the data back to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", name);
            resultIntent.putExtra("studentId", studentId);
            resultIntent.putExtra("number", number);

            // Set the result and finish the activity
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
