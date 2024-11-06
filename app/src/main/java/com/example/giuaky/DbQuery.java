package com.example.giuaky;

import android.content.Context;
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

public class DbQuery {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static List<User> userList = new ArrayList<>();

    public static void loadUserAccount(MyCompleteListener myCompleteListener){
        userList.clear();
        db.collection("USERS").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            userList.add(new User(
                                    doc.getString("ROLE"),
                                    doc.getString("NAME"),
                                    doc.getString("EMAIL"),
                                    doc.getString("PHONE_NUMBER"),
                                    doc.getLong("AGE").intValue(),
                                    doc.getString("STATUS")
                            ));
                        }
                        myCompleteListener.onSuccess();

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
