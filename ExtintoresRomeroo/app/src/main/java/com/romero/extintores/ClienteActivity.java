package com.romero.extintores;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClienteActivity extends AppCompatActivity {
    private TextView tvWelcome, tvAlerta;
    private Button btnAgregarExtintor, btnMisExtintores, btnSolicitarServicio, btnCerrarSesion;
    private RecyclerView rvExtintores;
    private ExtintorAdapter adapter;
    private List<Extintor> extintoresList;
    private RequestQueue requestQueue;
    private int userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        // Obtener datos de sesión
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = prefs.getInt("user_id", 0);
        userName = prefs.getString("name", "Cliente");

        tvWelcome = findViewById(R.id.tvWelcome);
        tvAlerta = findViewById(R.id.tvAlerta);
        btnAgregarExtintor = findViewById(R.id.btnAgregarExtintor);
        btnMisExtintores = findViewById(R.id.btnMisExtintores);
        btnSolicitarServicio = findViewById(R.id.btnSolicitarServicio);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        rvExtintores = findViewById(R.id.rvExtintores);

        tvWelcome.setText("Bienvenido, " + userName);
        extintoresList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        setupRecyclerView();
        cargarExtintores();

        btnAgregarExtintor.setOnClickListener(v -> {
            Intent intent = new Intent(ClienteActivity.this, AgregarExtintorActivity.class);
            startActivity(intent);
        });

        btnMisExtintores.setOnClickListener(v -> cargarExtintores());

        btnSolicitarServicio.setOnClickListener(v -> {
            if (extintoresList.isEmpty()) {
                Toast.makeText(this, "Primero debes agregar un extintor", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ClienteActivity.this, SolicitarServicioActivity.class);
                startActivity(intent);
            }
        });

        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(ClienteActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupRecyclerView() {
        adapter = new ExtintorAdapter(this, extintoresList);
        rvExtintores.setLayoutManager(new LinearLayoutManager(this));
        rvExtintores.setAdapter(adapter);
    }

    private void cargarExtintores() {
        String url = ApiConfig.GET_EXTINTORES + "?user_id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("extintores");
                            extintoresList.clear();
                            int alertas = 0;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Extintor extintor = new Extintor();
                                extintor.setId(obj.getInt("id"));
                                extintor.setTipo(obj.getString("tipo"));
                                extintor.setCapacidad(obj.getString("capacidad"));
                                extintor.setUbicacion(obj.getString("ubicacion"));
                                extintor.setFechaVencimiento(obj.getString("fecha_vencimiento"));
                                extintor.setDiasRestantes(obj.getInt("dias_restantes"));
                                extintor.setAlerta(obj.getBoolean("alerta"));
                                extintor.setVencido(obj.getBoolean("vencido"));
                                extintor.setImagenUrl(obj.optString("imagen_url", "default_extintor.jpg"));

                                if (extintor.isAlerta() || extintor.isVencido()) {
                                    alertas++;
                                }

                                extintoresList.add(extintor);
                            }

                            adapter.notifyDataSetChanged();

                            if (alertas > 0) {
                                tvAlerta.setText("⚠️ Tienes " + alertas + " extintor(es) por vencer o vencidos");
                                tvAlerta.setVisibility(TextView.VISIBLE);
                            } else {
                                tvAlerta.setVisibility(TextView.GONE);
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(ClienteActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ClienteActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarExtintores();
    }
}