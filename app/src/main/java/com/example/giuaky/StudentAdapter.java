package com.example.giuaky;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private ArrayList<Student> studentList;

    public StudentAdapter(ArrayList<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.studentlist, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.nameTextView.setText(student.getName());
        holder.studentIdTextView.setText("MSSV: " + student.getStudentId());
        holder.numberTextView.setText("STT: " + (position + 1)); // Assuming position + 1 as STT

        // You can load the image using a library like Glide or Picasso if you have URLs
        // holder.avatarImageView.setImageResource(R.mipmap.ic_launcher); // Placeholder for now
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView nameTextView;
        TextView studentIdTextView;
        TextView numberTextView;
        CheckBox selectCheckBox;

        public StudentViewHolder(View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            studentIdTextView = itemView.findViewById(R.id.studentidTextView);
            numberTextView = itemView.findViewById(R.id.numberTextView);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
        }
    }

    public void updateList(ArrayList<Student> newList) {
        studentList = newList;
        notifyDataSetChanged();
    }
}