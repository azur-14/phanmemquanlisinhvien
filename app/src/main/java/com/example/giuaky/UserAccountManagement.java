package com.example.giuaky;

import static com.example.giuaky.DbQuery.userList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserAccountManagement extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rcvAccountUser;
    private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_account_management);

        rcvAccountUser = findViewById(R.id.rcvAccountUser);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcvAccountUser.setLayoutManager(layoutManager);


        DbQuery.loadUserAccount(new MyCompleteListener(){
            @Override
            public void onSuccess() {
                userAdapter = new UserAdapter(userList);
                rcvAccountUser.setAdapter(userAdapter);
            }

            @Override
            public void onFailure() {
                Toast.makeText(UserAccountManagement.this,"Đã xảy ra lỗi khi load dữ liệu người đùng ! Vui lòng thử lại sau",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.Import){
            Intent intent = new Intent(UserAccountManagement.this, AddNewUserAccountActivity.class);
            startActivity(intent);
        }
        if(id == R.id.Export){
            ///
        }
        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return true;
    }
}