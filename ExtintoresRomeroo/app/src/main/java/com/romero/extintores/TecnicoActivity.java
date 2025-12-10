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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TecnicoActivity extends AppCompatActivity {
    private TextView tvWelcome, tvNotificaciones;
    private Button btnActualizar, btnCerrarSesion;
    private RecyclerView rvServicios;
    private ServicioAdapter adapter;
    private List<Servicio> serviciosList;
    private RequestQueue requestQueue;
    private String userName;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecnico);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userName = prefs.getString("name", "Técnico");
        userId = prefs.getInt("user_id", 0);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvNotificaciones = findViewById(R.id.tvNotificaciones);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        rvServicios = findViewById(R.id.rvServicios);

        tvWelcome.setText("Bienvenido, " + userName);

        requestQueue = Volley.newRequestQueue(this);
        serviciosList = new ArrayList<>();

        setupRecyclerView();
        cargarServicios();

        btnActualizar.setOnClickListener(v -> cargarServicios());

        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(TecnicoActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupRecyclerView() {
        adapter = new ServicioAdapter(this, serviciosList, servicio -> {
            mostrarDialogoActualizarServicio(servicio);
        });
        rvServicios.setLayoutManager(new LinearLayoutManager(this));
        rvServicios.setAdapter(adapter);
    }

    private void cargarServicios() {
        String url = ApiConfig.GET_SERVICIOS + "?role=tecnico";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("servicios");
                            serviciosList.clear();
                            int pendientes = 0;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Servicio servicio = new Servicio();
                                servicio.setId(obj.getInt("id"));
                                servicio.setClientName(obj.getString("client_name"));
                                servicio.setClientEmail(obj.getString("client_email"));
                                servicio.setTipo(obj.getString("tipo"));
                                servicio.setCapacidad(obj.getString("capacidad"));
                                servicio.setUbicacion(obj.getString("ubicacion"));
                                servicio.setEstado(obj.getString("estado"));
                                servicio.setFechaServicio(obj.optString("fecha_servicio", ""));
                                servicio.setTecnicoName(obj.optString("tecnico_name", "Sin asignar"));

                                if (servicio.getEstado().equals("Asignado") || servicio.getEstado().equals("En Proceso")) {
                                    pendientes++;
                                }

                                serviciosList.add(servicio);
                            }

                            adapter.notifyDataSetChanged();

                            if (pendientes > 0) {
                                tvNotificaciones.setText("🔔 Tienes " + pendientes + " servicio(s) por atender");
                                tvNotificaciones.setBackgroundColor(android.graphics.Color.parseColor("#FF6F00"));
                                tvNotificaciones.setVisibility(TextView.VISIBLE);
                            } else {
                                tvNotificaciones.setText("✅ No hay servicios pendientes");
                                tvNotificaciones.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"));
                                tvNotificaciones.setVisibility(TextView.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(TecnicoActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(TecnicoActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    private void mostrarDialogoActualizarServicio(Servicio servicio) {
        String mensaje = "Cliente: " + servicio.getClientName() +
                "\nEmail: " + servicio.getClientEmail() +
                "\nExtintor: " + servicio.getTipo() + " - " + servicio.getCapacidad() +
                "\nUbicación: " + servicio.getUbicacion() +
                "\nFecha programada: " + servicio.getFechaServicio();

        if (servicio.getEstado().equals("Pendiente")) {
            Toast.makeText(this, "⏳ Este servicio aún no ha sido asignado por el administrador", Toast.LENGTH_LONG).show();
        } else if (servicio.getEstado().equals("Asignado")) {
            // Servicio asignado → puede marcar "En Proceso"
            new AlertDialog.Builder(this)
                    .setTitle("🔔 Servicio Asignado")
                    .setMessage(mensaje + "\n\n¿Deseas iniciar este servicio?")
                    .setPositiveButton("🔧 MARCAR EN PROCESO", (dialog, which) -> {
                        actualizarEstadoServicio(servicio.getId(), "En Proceso");
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else if (servicio.getEstado().equals("En Proceso")) {
            // Servicio en proceso → puede marcar "Completado"
            new AlertDialog.Builder(this)
                    .setTitle("🔧 Servicio En Proceso")
                    .setMessage(mensaje + "\n\n¿Ya terminaste este servicio?")
                    .setPositiveButton("✅ MARCAR COMPLETADO", (dialog, which) -> {
                        actualizarEstadoServicio(servicio.getId(), "Completado");
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else if (servicio.getEstado().equals("Completado")) {
            Toast.makeText(this, "🎉 Este servicio ya fue completado", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarEstadoServicio(int servicioId, String nuevoEstado) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.ACTUALIZAR_SERVICIO,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            String emoji = nuevoEstado.equals("Completado") ? "🎉" : "🔧";
                            Toast.makeText(TecnicoActivity.this, emoji + " Servicio actualizado a: " + nuevoEstado, Toast.LENGTH_SHORT).show();
                            cargarServicios();
                        } else {
                            Toast.makeText(TecnicoActivity.this, "❌ Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(TecnicoActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(TecnicoActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("servicio_id", String.valueOf(servicioId));
                params.put("estado", nuevoEstado);
                params.put("tecnico_id", String.valueOf(userId));
                return params;
            }
        };

        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarServicios();
    }
}