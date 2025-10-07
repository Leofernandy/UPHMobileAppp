package com.example.uphmobileappp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uphmobileappp.api.ApiResponse;
import com.example.uphmobileappp.api.ApiResponseKabupaten;
import com.example.uphmobileappp.api.ApiService;
import com.example.uphmobileappp.model.Kabupaten;
import com.example.uphmobileappp.model.Provinsi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button btnLogout;
    TextView txvUser;

    Spinner sprProvinsi;
    List<Provinsi> provinsiList = new ArrayList<>();
    List<String> namaProvinsi = new ArrayList<>();
    ArrayAdapter<String> provinsiAdapter;

    Spinner sprKabupaten;
    List<Kabupaten> kabupatenList = new ArrayList<>();
    List<String> namaKabupaten = new ArrayList<>();
    ArrayAdapter<String> kabupatenAdapter;

    ListView listKabupaten;
    ArrayAdapter<String> listAdapter;

    ApiService apiService;

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

        // Spinner Provinsi
        sprProvinsi = findViewById(R.id.sprProvinsi);
        provinsiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, namaProvinsi);
        provinsiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprProvinsi.setAdapter(provinsiAdapter);

        // Spinner Kabupaten
        sprKabupaten = findViewById(R.id.sprKabupaten);
        kabupatenAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, namaKabupaten);
        kabupatenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sprKabupaten.setAdapter(kabupatenAdapter);

        // ListView untuk menampilkan 10 kabupaten pertama
        listKabupaten = findViewById(R.id.listKabupaten);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listKabupaten.setAdapter(listAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://wilayah.id/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Load Provinsi
        loadProvinsi();



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

    private void loadProvinsi() {
        apiService.getProvinsi().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provinsiList = response.body().getData();
                    namaProvinsi.clear();
                    for (Provinsi p : provinsiList) {
                        if (p.getName() != null) {
                            namaProvinsi.add(p.getName());
                        }
                    }
                    provinsiAdapter.notifyDataSetChanged();

                    sprProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Provinsi selected = provinsiList.get(position);
                            Log.d("Provinsi", selected.getCode() + " - " + selected.getName());
                            loadKabupaten(selected.getCode());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal ambil provinsi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadKabupaten(String provinsiCode) {
        apiService.getKabupaten(provinsiCode).enqueue(new Callback<ApiResponseKabupaten>() {
            @Override
            public void onResponse(Call<ApiResponseKabupaten> call, Response<ApiResponseKabupaten> response) {
                if (response.isSuccessful() && response.body() != null) {
                    kabupatenList = response.body().getData();
                    namaKabupaten.clear();

                    for (Kabupaten k : kabupatenList) {
                        if (k.getName() != null) {
                            namaKabupaten.add(k.getName());
                        }
                    }
                    kabupatenAdapter.notifyDataSetChanged();

                    // --- tampilkan 10 kabupaten pertama di ListView ---
                    List<String> firstTen = new ArrayList<>();
                    for (int i = 0; i < Math.min(10, namaKabupaten.size()); i++) {
                        firstTen.add(namaKabupaten.get(i));
                    }
                    listAdapter.clear();
                    listAdapter.addAll(firstTen);
                    listAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onFailure(Call<ApiResponseKabupaten> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal ambil kabupaten: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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