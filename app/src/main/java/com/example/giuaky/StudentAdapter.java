package com.example.giuaky;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> studentList; // Original list of students
    private List<Student> selectedStudents; // List to keep track of selected students

    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
        this.selectedStudents = new ArrayList<>(); // Initialize the ArrayList
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.img.setImageResource(R.drawable.student_image); // Placeholder image
        holder.name.setText("Họ và tên: " + student.getName());
        holder.faculty.setText("Khoa: " + student.getFaculty());
        holder.mssv.setText("MSSV: " + student.getStudentID());

        // Set the checkbox state
        holder.selectedStudent.setOnCheckedChangeListener(null); // Prevent triggering listener
        holder.selectedStudent.setChecked(selectedStudents.contains(student));

        holder.selectedStudent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedStudents.add(student);
            } else {
                selectedStudents.remove(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = studentList.size();
        Log.d("StudentAdapter", "Item count: " + count);
        return count;
    }

    public List<Student> getSelectedStudents() {
        return selectedStudents; // Return the list of selected students
    }

    public void clearSelections() {
        selectedStudents.clear(); // Clear the selected students
        notifyDataSetChanged(); // Refresh the adapter
    }

    // Method to update the student list
    @SuppressLint("NotifyDataSetChanged")
    public void updateStudentList(List<Student> newStudentList) {
        this.studentList = newStudentList;
        clearSelections();
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView name;
        private TextView faculty;
        private TextView mssv;
        private CheckBox selectedStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.txtName);
            faculty = itemView.findViewById(R.id.txtFaculty);
            mssv = itemView.findViewById(R.id.txtMSSV);
            selectedStudent = itemView.findViewById(R.id.selectedStudent);
        }
    }
}