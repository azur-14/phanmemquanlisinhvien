package com.example.giuaky;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DbQuery {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static List<User> userList = new ArrayList<>();

//    public static void loadUserAccount(final MyCompleteListener myCompleteListener){
//        userList.clear();
//
//        db.collection("USERS")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();
//
//                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
//                            docList.put(doc.getId(),doc);
//                        }
//                        QueryDocumentSnapshot userListItem = docList.get("TOTAL_USER");
//
//                        int totalUser = userListItem.getLong("count").intValue();
//
//                        for (int i = 1; i <= totalUser; i++) {
//                            String userId = userListItem.getString("USER" + String.valueOf(i) + "_ID");
//                            QueryDocumentSnapshot userDoc = docList.get(userId);
//                            String role = userDoc.getString("ROLE");
//                            String name = userDoc.getString("NAME");
//                            String email = userDoc.getString("EMAIL");
//                            String phoneNumber = userDoc.getString("PHONE_NUMBER");
//                            Long age = userDoc.getLong("AGE");
//                            String status = userDoc.getString("STATUS");
//                            userList.add(new User(role, name, email, phoneNumber, age.intValue(), status));
//
//                        }
//                        myCompleteListener.onSuccess();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        myCompleteListener.onFailure();
//                    }
//                });
//    }

    public static void loadUserAccount(final MyCompleteListener myCompleteListener) {
        userList.clear();
        db.collection("USERS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();

                        // Lưu trữ các tài liệu vào docList
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            docList.put(doc.getId(), doc);
                        }

                        // Lấy thông tin tổng số người dùng
                        QueryDocumentSnapshot userListItem = docList.get("TOTAL_USER");
                        if (userListItem != null) {
                            long totalUser = userListItem.getLong("count");

                            for (int i = 1; i <= totalUser; i++) {
                                String userId = userListItem.getString("USER" + i + "_ID");
                                QueryDocumentSnapshot doc = docList.get(userId);

                                if (doc != null) {
                                    String role = doc.getString("ROLE");
                                    String name = doc.getString("NAME");
                                    String email = doc.getString("EMAIL");
                                    String phoneNumber = doc.getString("PHONE_NUMBER");
                                    Long age = doc.getLong("AGE");
                                    String status = doc.getString("STATUS");

                                    userList.add(new User(role, name, email, phoneNumber, age != null ? age.intValue() : 0, status));
                                } else {
                                    Log.w("LoadUserAccount", "Không tìm thấy user với ID: " + userId);
                                }
                            }

                            Log.d("LoadUserAccount", "Đã thêm " + userList.size() + " người dùng vào danh sách.");
                            myCompleteListener.onSuccess();
                        } else {
                            Log.w("LoadUserAccount", "Không tìm thấy tài liệu TOTAL_USER.");
                            myCompleteListener.onFailure();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LoadUserAccount", "Lỗi khi tải dữ liệu người dùng: " + e.getMessage());
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static Task<String> getRole() {
        final TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            db.collection("USERS").document(uid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            // Lấy giá trị "role" và set vào taskCompletionSource
                            String role = documentSnapshot.getString("ROLE");
                            if (role != null) {
                                taskCompletionSource.setResult(role);
                            } else {
                                taskCompletionSource.setException(new Exception("Role not found"));
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            taskCompletionSource.setException(e);
                        }
                    });
        } else {
            taskCompletionSource.setException(new Exception("User UID is null"));
        }

        return taskCompletionSource.getTask();
    }
    public static void loadStudents(final MyCompleteListener myCompleteListener) {
        userList.clear();
        db.collection("USERS")
                .whereEqualTo("ROLE", "student")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String role = doc.getString("ROLE");
                            String name = doc.getString("NAME");
                            String email = doc.getString("EMAIL");
                            String phoneNumber = doc.getString("PHONE_NUMBER");
                            Long age = doc.getLong("AGE");
                            String status = doc.getString("STATUS");
                            String studentID = doc.getString("STUDENT_ID");
                            String faculty = doc.getString("FACULTY");
                            String major = doc.getString("MAJOR");

                            // Create a Student object and add it to the list
                            Student student = new Student(role, name, email, phoneNumber, status, studentID, age != null ? age.intValue() : 0, faculty, major);
                            userList.add(student);
                        }
                        myCompleteListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LoadStudents", "Error loading student data: " + e.getMessage());
                        myCompleteListener.onFailure();
                    }
                });
    }
    public static void deleteStudent(String studentID, final MyCompleteListener myCompleteListener) {
        db.collection("USERS")
                .whereEqualTo("STUDENT_ID", studentID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                    myCompleteListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteStudent", "Error deleting student: " + e.getMessage());
                    myCompleteListener.onFailure();
                });
    }
    public static void addStudent(Student student, final MyCompleteListener myCompleteListener) {
        db.collection("USERS")
                .document(student.getStudentID()) // Assuming STUDENT_ID is unique
                .set(student)
                .addOnSuccessListener(aVoid -> {
                    Log.d("DbQuery", "Student added successfully: " + student.getStudentID());
                    myCompleteListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("DbQuery", "Error adding student: " + e.getMessage());
                    myCompleteListener.onFailure();
                });
    }
}
