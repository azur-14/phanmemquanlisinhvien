package com.example.studentmanagement.adapter;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> studentList;
    private List<Student> selectedStudents;
    private final OnUserClickListener onUserClickListener;

    public StudentAdapter(List<Student> studentList, OnUserClickListener listener) {
        this.studentList = studentList;
        this.selectedStudents = new ArrayList<>();
        this.onUserClickListener = listener; // Initialize the listener
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_student, parent, false);
        return new StudentViewHolder(view, onUserClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.img.setImageResource(R.drawable.avatar); // Placeholder image
        holder.name.setText("Họ và tên: " + student.getName());
        holder.faculty.setText("Khoa: " + student.getFaculty());
        holder.mssv.setText("MSSV: " + student.getStudentID());

        // Set the checkbox state
        holder.selectedStudent.setOnCheckedChangeListener(null);
        holder.selectedStudent.setChecked(selectedStudents.contains(student));
        holder.selectedStudent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedStudents.add(student);
            } else {
                selectedStudents.remove(student);
            }
        });

        // Bind item click listeners for edit, delete, and info actions
        holder.itemView.setOnClickListener(v -> onUserClickListener.onSeeDetailedUserInfoClick(student.getStudentID()));
        holder.itemView.setOnLongClickListener(v -> {
            onUserClickListener.onEditUserClick(student.getStudentID());
            return true;
        });
        holder.selectedStudent.setOnLongClickListener(v -> {
            onUserClickListener.onDeleteUserClick(student.getStudentID());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public List<Student> getSelectedStudents() {
        return selectedStudents;
    }

    public void clearSelections() {
        selectedStudents.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateStudentList(List<Student> newStudentList) {
        this.studentList = newStudentList;
        clearSelections();
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;
        private final TextView name;
        private final TextView faculty;
        private final TextView mssv;
        private final CheckBox selectedStudent;

        public StudentViewHolder(@NonNull View itemView, OnUserClickListener listener) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.txtName);
            faculty = itemView.findViewById(R.id.txtFaculty);
            mssv = itemView.findViewById(R.id.txtMSSV);
            selectedStudent = itemView.findViewById(R.id.selectedStudent);
        }
    }

    public interface OnUserClickListener {
        void onEditUserClick(String uid);
        void onDeleteUserClick(String uid);
        void onSeeDetailedUserInfoClick(String uid);
    }
}
