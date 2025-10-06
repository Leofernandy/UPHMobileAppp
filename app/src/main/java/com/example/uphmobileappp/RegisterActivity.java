package com.example.uphmobileappp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUserName, edtPassword;

    EditText edtNama, edtStudentID, edtTempatLahir, edtTanggalLahir;
    TextView txvLogin;
    Button btnRegister;

    private FirebaseAuth mAuth;
// ...
// Initialize Firebase Auth
//    mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        txvLogin = findViewById(R.id.txvLogin);
        btnRegister = findViewById(R.id.btnRegister);
        edtNama = findViewById(R.id.edtNama);
        edtStudentID = findViewById(R.id.edtStudentID);
        edtTempatLahir = findViewById(R.id.edtTempatLahir);
        edtTanggalLahir = findViewById(R.id.edtTanggalLahir);

        txvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtUserName.getText().toString().trim();
                String password = edtPassword.getText().toString();
                register(email, password);

                String nama = edtNama.getText().toString().trim();
                String studentID = edtStudentID.getText().toString().trim();
                String tempatLahir = edtTempatLahir.getText().toString().trim();
                String tanggalLahir = edtTanggalLahir.getText().toString().trim();
                registerStudent(nama , studentID, tempatLahir, tanggalLahir, email);
            }
        });
    }

    public void registerStudent(String nama, String studentID, String tempatLahir , String tanggalLahir, String email){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> student = new HashMap<>();
        student.put("Email", email);
        student.put("Nama", nama);
        student.put("StudentID", studentID);
        student.put("TempatLahir", tempatLahir);
        student.put("TanggalLahir", tanggalLahir);


// Add a new document with a generated ID
        db.collection("students")
                .add(student)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("STUDENT", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("STUDENT", "Error adding document", e);
                    }
                });

    }

    public void register(String email, String password){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("R", "RegisterWithCustomToken:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("R", "RegisterWithCustomToken:failure",
                                    task.getException());
                        }
                    }
                });
    }

    public void toLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}