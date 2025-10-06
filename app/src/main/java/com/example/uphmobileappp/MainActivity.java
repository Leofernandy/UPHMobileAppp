package com.example.uphmobileappp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    Button btnLogout;
    TextView txvUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        btnLogout = findViewById(R.id.btnLogout);
        txvUser = findViewById(R.id.txvUser);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                toLogin();
            }
        });

        String email = getIntent().getStringExtra("email");
        Log.d("EMAIL_MAIN", "Email diterima: " + email);
        readData(email);

    }

    public void readData(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("students")
                .whereEqualTo("Email", email) // 🔥 filter berdasarkan email login
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("STUDENT", "Document data: " + document.getData());
                                    Object namaObj = document.get("Nama");
                                    if (namaObj != null) {
                                        txvUser.setText("Halo, " + namaObj.toString());
                                    } else {
                                        txvUser.setText("Halo, pengguna tanpa nama");
                                    }
                                }
                            } else {
                                Log.w("STUDENT", "Tidak ada data student dengan email: " + email);
                                txvUser.setText("Halo, data tidak ditemukan");
                            }
                        } else {
                            Log.w("STUDENT", "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    public void toLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}