package com.romero.extintores;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolicitarServicioActivity extends AppCompatActivity {
    private Spinner spinnerExtintor;
    private EditText etFechaServicio, etHoraServicio, etDescripcion;
    private Button btnEnviarSolicitud;
    private RequestQueue requestQueue;
    private int userId;
    private List<Extintor> extintoresList;
    private List<String> extintoresNombres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_servicio);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = prefs.getInt("user_id", 0);

        spinnerExtintor = findViewById(R.id.spinnerExtintor);
        etFechaServicio = findViewById(R.id.etFechaServicio);
        etHoraServicio = findViewById(R.id.etHoraServicio);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnEnviarSolicitud = findViewById(R.id.btnEnviarSolicitud);

        requestQueue = Volley.newRequestQueue(this);
        extintoresList = new ArrayList<>();
        extintoresNombres = new ArrayList<>();

        cargarExtintores();

        btnEnviarSolicitud.setOnClickListener(v -> solicitarServicio());
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
                            extintoresNombres.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Extintor extintor = new Extintor();
                                extintor.setId(obj.getInt("id"));
                                extintor.setTipo(obj.getString("tipo"));
                                extintor.setCapacidad(obj.getString("capacidad"));
                                extintor.setUbicacion(obj.getString("ubicacion"));
                                extintoresList.add(extintor);

                                String nombre = extintor.getTipo() + " - " + extintor.getCapacidad() + " (" + extintor.getUbicacion() + ")";
                                extintoresNombres.add(nombre);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_item, extintoresNombres);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerExtintor.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        Toast.makeText(SolicitarServicioActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(SolicitarServicioActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    private void solicitarServicio() {
        if (extintoresList.isEmpty()) {
            Toast.makeText(this, "No tienes extintores registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        int posicion = spinnerExtintor.getSelectedItemPosition();
        int extintorId = extintoresList.get(posicion).getId();

        String fecha = etFechaServicio.getText().toString().trim();
        String hora = etHoraServicio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Complete fecha y hora del servicio", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaServicio = fecha + " " + hora + ":00";

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.SOLICITAR_SERVICIO,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(SolicitarServicioActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        if (jsonObject.getBoolean("success")) {
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(SolicitarServicioActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(SolicitarServicioActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", String.valueOf(userId));
                params.put("extintor_id", String.valueOf(extintorId));
                params.put("fecha_servicio", fechaServicio);
                params.put("descripcion", descripcion);
                return params;
            }
        };

        requestQueue.add(request);
    }
}