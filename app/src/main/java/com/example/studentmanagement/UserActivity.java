package com.example.studentmanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.adapter.UserAdapter;
import com.example.studentmanagement.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {
    RecyclerView rv;
    UserAdapter adapter;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference usersRef = firebaseFirestore.collection("Users");
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        this.sharedPref = getSharedPreferences(getString(R.string.preference_login_key), Context.MODE_PRIVATE);

        rv = findViewById(R.id.rv_users);

        // set layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(gridLayoutManager);

        // set adapter
        ArrayList<User> users = new ArrayList<>();
        Query query = usersRef.whereNotEqualTo("role", 2);
        // simply get the data from query to the adapter
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        adapter = new UserAdapter(options, this);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_management, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String accountUID = sharedPref.getString(getString(R.string.saved_account_uid_key), "");

        if (item.getItemId() == R.id.create) {
            Intent intent = new Intent(UserActivity.this, CreateEditUserActivity.class);
            intent.putExtra("action", "add");
            startActivity(intent);
        } else if (item.getItemId() == R.id.account_management) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.student_management) {
            Intent intent = new Intent(this, StudentManagementActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.info) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("accountUID", accountUID);
            startActivity(intent);
        } else if (item.getItemId() == R.id.log_out) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // read docs for this
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    // why use onDestroy instead of onStop (https://github.com/firebase/FirebaseUI-Android/issues/2014)

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void onEditUserClick(String uid) {
        Intent intent = new Intent(this, CreateEditUserActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("action", "edit");
        startActivity(intent);
    }

    @Override
    public void onDeleteUserClick(String uid) {
        usersRef.document(uid).delete();
    }

    @Override
    public void onSeeDetailedUserInfoClick(String uid) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("userUID", uid);
        startActivity(intent);
    }
}