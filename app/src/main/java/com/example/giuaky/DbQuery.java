package com.example.giuaky;

import android.util.ArrayMap;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class DbQuery {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static List<User> userList = new ArrayList<>();
    public static List<Student> studentList = new ArrayList<>();
    public static List<Certificate> certificateList = new ArrayList<>();


    public static void loadStudent(final MyCompleteListener myCompleteListener) {
        studentList.clear();
        db.collection("STUDENTS")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();

                        // Store documents into docList
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            docList.put(doc.getId(), doc);
                        }

                        // Get total number of students
                        QueryDocumentSnapshot totalStudentsDoc = docList.get("TOTAL_STUDENTS");
                        if (totalStudentsDoc != null) {
                            long totalUser = totalStudentsDoc.getLong("COUNT");

                            for (int i = 1; i <= totalUser; i++) {
                                String studentIDKey = "STUDENT" + i + "_ID";
                                String studentID = totalStudentsDoc.getString(studentIDKey);

                                if (studentID == null) {
                                    Log.w("LoadStudent", "Student ID key missing for index: " + i);
                                    continue; // Skip this iteration if the ID is missing
                                }

                                QueryDocumentSnapshot studentDoc = docList.get(studentID);

                                if (studentDoc != null) {
                                    // Retrieve student details
                                    String mssv = studentDoc.getString("STUDENT_ID");
                                    Long age = studentDoc.getLong("AGE");
                                    String name = studentDoc.getString("NAME");
                                    String faculty = studentDoc.getString("FACULTY");
                                    String major = studentDoc.getString("MAJOR");
                                    String email = studentDoc.getString("EMAIL");
                                    String phonenumber = studentDoc.getString("PHONENUMBER");
                                    String image = studentDoc.getString("IMAGE");

                                    // Log loaded student data
                                    Log.d("LoadStudent", "Loaded Student: " + mssv + ", Name: " + name);
                                    Log.d("LoadStudent", "Total Students Count: " + totalUser);
                                    Log.d("LoadStudent", "Current Loaded Students: " + studentList.size());
                                    // Add the student to the list
                                    studentList.add(new Student(mssv, name, faculty, major, age != null ? age.intValue() : 0, phonenumber, email,image));
                                } else {
                                    Log.w("LoadStudent", "Student not found with ID: " + studentID);
                                }
                            }

                            myCompleteListener.onSuccess();
                        } else {
                            Log.e("LoadStudent", "TOTAL_STUDENTS document not found.");
                            myCompleteListener.onFailure();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("LoadStudent", "Error fetching student data", e);
                        myCompleteListener.onFailure();
                    }
                });
    }
    public static void updateStudent(Student student, final MyCompleteListener myCompleteListener) {
        if (student == null || student.getStudentID() == null || student.getStudentID().isEmpty()) {
            Log.e("DbQuery", "Student or Student ID is null or empty");
            myCompleteListener.onFailure();
            return;
        }

        db.collection("STUDENTS")
                .whereEqualTo("STUDENT_ID", student.getStudentID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> studentData = new HashMap<>();
                            studentData.put("NAME", student.getName());
                            studentData.put("FACULTY", student.getFaculty());
                            studentData.put("MAJOR", student.getMajor());
                            studentData.put("AGE", student.getAge());
                            studentData.put("EMAIL", student.getEmail());
                            studentData.put("PHONENUMBER", student.getPhoneNumber());
                            studentData.put("IMAGE", student.getImage()); // Add the image data

                            db.collection("STUDENTS")
                                    .document(document.getId())
                                    .update(studentData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("DbQuery", "Student updated successfully: " + student.getStudentID());
                                        myCompleteListener.onSuccess();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("DbQuery", "Error updating student: " + e.getMessage());
                                        myCompleteListener.onFailure();
                                    });
                            return; // Exit after updating
                        }
                    } else {
                        Log.e("DbQuery", "Student document not found for STUDENT_ID: " + student.getStudentID());
                        myCompleteListener.onFailure();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DbQuery", "Error fetching student document: " + e.getMessage());
                    myCompleteListener.onFailure();
                });
    }
    public static Task<Integer> getRole() {
        final TaskCompletionSource<Integer> taskCompletionSource = new TaskCompletionSource<>();
        String uid = FirebaseAuth.getInstance().getUid();

        // Log the UID
        if (uid != null) {
            Log.d("DbQuery", "User UID: " + uid); // Log the UID

            db.collection("USERS").document(uid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            // Kiểm tra nếu tài liệu tồn tại
                            if (documentSnapshot.exists()) {
                                // Lấy giá trị "role" như một Integer
                                Long roleLong = documentSnapshot.getLong("role"); // Đảm bảo tên thuộc tính là "role"
                                if (roleLong != null) {
                                    taskCompletionSource.setResult(roleLong.intValue()); // Trả về giá trị int
                                } else {
                                    taskCompletionSource.setException(new Exception("Role not found"));
                                }
                            } else {
                                taskCompletionSource.setException(new Exception("User document does not exist"));
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

    public static void deleteStudent(String studentID, final MyCompleteListener myCompleteListener) {
        // Tham chiếu đến tài liệu sinh viên và tài liệu tổng số sinh viên
        DocumentReference studentRef = db.collection("STUDENTS").document(studentID);
        DocumentReference totalStudentsRef = db.collection("STUDENTS").document("TOTAL_STUDENTS");

        totalStudentsRef.get().addOnSuccessListener(totalStudentsDoc -> {
            if (totalStudentsDoc.exists()) {
                long count = totalStudentsDoc.getLong("COUNT");
                // Thực hiện giao dịch để xóa sinh viên và cập nhật số lượng
                db.runTransaction(transaction -> {
                    transaction.delete(studentRef); // Xóa sinh viên

                    transaction.update(totalStudentsRef, "COUNT", count - 1); // Cập nhật COUNT

                    // Cập nhật các trường STUDENT{i}_ID trong tài liệu TOTAL_STUDENTS
                    for (int i = 1; i <= count; i++) {
                        String studentKey = "STUDENT" + i + "_ID";
                        String storedID = totalStudentsDoc.getString(studentKey);
                        if (studentID.equals(storedID)) {
                            transaction.update(totalStudentsRef, studentKey, FieldValue.delete());
                            break;
                        }
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> {
                    Log.d("DbQuery", "Student deleted successfully: " + studentID);
                    myCompleteListener.onSuccess();
                }).addOnFailureListener(e -> {
                    Log.e("DbQuery", "Error deleting student: " + e.getMessage());
                    myCompleteListener.onFailure();
                });
            } else {
                Log.e("DbQuery", "TOTAL_STUDENTS document not found.");
                myCompleteListener.onFailure();
            }
        }).addOnFailureListener(e -> {
            Log.e("DbQuery", "Error fetching TOTAL_STUDENTS: " + e.getMessage());
            myCompleteListener.onFailure();
        });
    }
    public static void addStudent(Student student, final MyCompleteListener myCompleteListener) {
        if (student == null || student.getStudentID() == null || student.getStudentID().isEmpty()) {
            Log.e("DbQuery", "Student or Student ID is null or empty");
            myCompleteListener.onFailure();
            return;
        }

        DocumentReference totalStudentsRef = db.collection("STUDENTS").document("TOTAL_STUDENTS");

        db.runTransaction(transaction -> {
            DocumentSnapshot totalStudentsDoc = transaction.get(totalStudentsRef);

            Long currentCount = totalStudentsDoc.getLong("COUNT");
            if (currentCount == null) {
                currentCount = 0L;
                transaction.set(totalStudentsRef, Collections.singletonMap("COUNT", currentCount));
            }

            // Update count and add student ID reference
            transaction.update(totalStudentsRef, "COUNT", currentCount + 1);
            String studentKey = "STUDENT" + (currentCount + 1) + "_ID";
            transaction.update(totalStudentsRef, studentKey, student.getStudentID());

            return null;
        }).addOnSuccessListener(aVoid -> {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("STUDENT_ID", student.getStudentID());
            studentData.put("NAME", student.getName());
            studentData.put("FACULTY", student.getFaculty());
            studentData.put("MAJOR", student.getMajor());
            studentData.put("AGE", student.getAge());
            studentData.put("EMAIL", student.getEmail());
            studentData.put("PHONENUMBER", student.getPhoneNumber());

            // Thêm thông tin hình ảnh
            if (student.getImage() != null && !student.getImage().isEmpty()) {
                studentData.put("IMAGE", student.getImage()); // Lưu hình ảnh ở đây
            }

            db.collection("STUDENTS")
                    .document(student.getStudentID())
                    .set(studentData)
                    .addOnSuccessListener(aVoid1 -> {
                        Log.d("DbQuery", "Student added successfully: " + student.getStudentID());
                        myCompleteListener.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("DbQuery", "Error adding student to Firestore: " + e.getMessage(), e);
                        myCompleteListener.onFailure();
                    });
        }).addOnFailureListener(e -> {
            Log.e("DbQuery", "Transaction error (count update): " + e.getMessage(), e);
            myCompleteListener.onFailure();
        });
    }
    public static void addCertificate(Certificate certificate, final MyCompleteListener myCompleteListener) {
        if (certificate == null) {
            Log.e("DbQuery", "Certificate is null");
            myCompleteListener.onFailure();
            return;
        }

        // Generate a unique ID for the new certificate if not provided
        if (certificate.getCertificateID() == null || certificate.getCertificateID().isEmpty()) {
            String uniqueId = UUID.randomUUID().toString();
            certificate.setCertificateID(uniqueId);
        }

        // Prepare certificate data for Firestore
        Map<String, Object> certificateData = new HashMap<>();
        certificateData.put("CERTIFICATE_ID", certificate.getCertificateID());
        certificateData.put("STUDENT_ID", certificate.getStudentID());
        certificateData.put("NAME", certificate.getName());
        certificateData.put("ISSUED_BY", certificate.getIssuedBy());
        certificateData.put("DATE_ISSUED", certificate.getDateIssued());
        certificateData.put("SCORE", certificate.getScore());
        certificateData.put("REMARKS", certificate.getRemarks());

        // Add certificate data to Firestore
        db.collection("CERTIFICATION")
                .document(certificate.getCertificateID())
                .set(certificateData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("DbQuery", "Certificate added successfully: " + certificate.getCertificateID());
                    myCompleteListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("DbQuery", "Error adding certificate to Firestore: " + e.getMessage(), e);
                    myCompleteListener.onFailure();
                });
    }
    public static void loadCertificates(final String studentID, final MyCompleteListener myCompleteListener) {
        certificateList.clear();  // Clear the current list of certificates

        Log.d("DbQuery", "Loading certificates for student ID: " + studentID);

        // Fetch certificates for the specific student from Firestore
        db.collection("CERTIFICATION")
                .whereEqualTo("STUDENT_ID", studentID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("DbQuery", "No certificates found for student ID: " + studentID);
                        myCompleteListener.onSuccess(); // Call onSuccess even if no certificates found
                        return;
                    }

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String certificateID = doc.getId();
                        String name = doc.getString("NAME");
                        String issuedBy = doc.getString("ISSUED_BY");
                        String dateIssued = doc.getString("DATE_ISSUED");
                        Double score = doc.getDouble("SCORE");
                        String remarks = doc.getString("REMARKS");

                        Log.d("DbQuery", "Loaded certificate: " + name + ", Issued by: " + issuedBy);

                        // Ensure that the data is not null before adding
                        if (name != null && issuedBy != null && dateIssued != null) {
                            certificateList.add(new Certificate(certificateID, studentID, name, issuedBy, dateIssued, score, remarks));
                        }
                    }

                    Log.d("DbQuery", "Total certificates loaded: " + certificateList.size());
                    myCompleteListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("DbQuery", "Error loading certificates: " + e.getMessage(), e);
                    myCompleteListener.onFailure();
                });
    }

    public static void deleteCertificate(String certificateID, final MyCompleteListener myCompleteListener) {
        if (certificateID == null || certificateID.isEmpty()) {
            Log.e("DbQuery", "Certificate ID is null or empty");
            myCompleteListener.onFailure();
            return;
        }

        db.collection("CERTIFICATION")
                .document(certificateID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("DbQuery", "Certificate deleted successfully: " + certificateID);
                    myCompleteListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("DbQuery", "Error deleting certificate: " + e.getMessage(), e);
                    myCompleteListener.onFailure();
                });
    }
    public static void updateCertificate(Certificate certificate, final MyCompleteListener myCompleteListener) {
        if (certificate == null || certificate.getCertificateID() == null || certificate.getCertificateID().isEmpty()) {
            Log.e("DbQuery", "Certificate or Certificate ID is null or empty");
            myCompleteListener.onFailure();
            return;
        }

        // Prepare the updated certificate data (excluding CERTIFICATE_ID and STUDENT_ID)
        Map<String, Object> certificateData = new HashMap<>();
        certificateData.put("NAME", certificate.getName());
        certificateData.put("ISSUED_BY", certificate.getIssuedBy());
        certificateData.put("DATE_ISSUED", certificate.getDateIssued());
        certificateData.put("SCORE", certificate.getScore());
        certificateData.put("REMARKS", certificate.getRemarks());

        // Update the certificate data in Firestore
        db.collection("CERTIFICATION")
                .document(certificate.getCertificateID()) // Ensure this is the correct document ID
                .update(certificateData) // Only update specified fields
                .addOnSuccessListener(aVoid -> {
                    Log.d("DbQuery", "Certificate updated successfully: " + certificate.getCertificateID());
                    myCompleteListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("DbQuery", "Error updating certificate: " + e.getMessage(), e);
                    myCompleteListener.onFailure();
                });
    }
}
