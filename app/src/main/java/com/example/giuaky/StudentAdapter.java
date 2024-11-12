package com.example.giuaky;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> studentList;

    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_student, parent, false);
        return new StudentViewHolder(view);
    }


    //for(int position = 0; position < teacherList.size(); position++)
    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.img.setImageResource(R.drawable.student_image);
        holder.name.setText("Họ và tên: " + student.getName());
        holder.faculty.setText("Khoa: " + student.getFaculty());
        holder.mssv.setText("MSSV: " + student.getStudentID());
        holder.major.setText("Ngành: " + student.getMajor());
        holder.selectedStudent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
                popupMenu.setGravity(Gravity.END); // Đưa menu ra lề phải

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView mssv, name, faculty, major;
        private LinearLayout selectedStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.txtName);
            faculty = itemView.findViewById(R.id.txtFaculty);
            mssv = itemView.findViewById(R.id.txtMSSV);
            major = itemView.findViewById(R.id.txtMajor);
            selectedStudent = itemView.findViewById(R.id.selectedStudent);
        }
    }
}

