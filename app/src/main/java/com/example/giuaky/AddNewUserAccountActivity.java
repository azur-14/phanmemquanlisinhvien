package com.example.giuaky;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

public class AddNewUserAccountActivity extends AppCompatActivity {
    AppCompatSpinner selectedDegree;
    AppCompatSpinner selectedFaculty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_teacher);

        selectedDegree = findViewById(R.id.list_degree);
        ArrayAdapter<CharSequence> adapterDegree = ArrayAdapter.createFromResource(this,R.array.degree, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapterDegree.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        selectedDegree.setAdapter(adapterDegree);

        selectedFaculty = findViewById(R.id.list_faculty);
        ArrayAdapter<CharSequence> adapterFaculty = ArrayAdapter.createFromResource(this,R.array.faculty, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapterFaculty.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        selectedFaculty.setAdapter(adapterFaculty);
        selectedFaculty.setDropDownVerticalOffset(260);




    }
}