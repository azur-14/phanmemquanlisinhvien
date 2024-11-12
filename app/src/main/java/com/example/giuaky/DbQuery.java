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
    public static List<Student> studentList = new ArrayList<>();

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

                            myCompleteListener.onSuccess();
                        } else {
                            myCompleteListener.onFailure();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myCompleteListener.onFailure();
                    }
                });
    }

    public static void loadStudent(final MyCompleteListener myCompleteListener) {
        studentList.clear();
        db.collection("STUDENTS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();

                        // Lưu trữ các tài liệu vào docList
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            docList.put(doc.getId(), doc);
                        }

                        // Lấy thông tin tổng số sinh viên
                        QueryDocumentSnapshot userListItem = docList.get("TOTAL_STUDENTS");
                        if (userListItem != null) {
                            long totalUser = userListItem.getLong("COUNT");

                            for (int i = 1; i <= totalUser; i++) {
                                String studentID = userListItem.getString("STUDENT" + i + "_ID");
                                QueryDocumentSnapshot doc = docList.get(studentID);

                                if (doc != null) {
                                    String mssv = doc.getString("STUDENT_ID");
                                    String name = doc.getString("NAME");
                                    String faculty = doc.getString("FACULTY");
                                    String major = doc.getString("MAJOR");

                                    studentList.add(new Student(mssv,name,faculty,major));
                                } else {
                                    Log.w("LoadStudent", "Không tìm thấy sinh viên với ID: " + studentID);
                                }
                            }

                            myCompleteListener.onSuccess();
                        } else {
                            myCompleteListener.onFailure();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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
}
