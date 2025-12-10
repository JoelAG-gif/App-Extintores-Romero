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

public class AdminActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnGestionarServicios, btnGestionarInventario, btnCerrarSesion;
    private RecyclerView rvServicios, rvProductos;
    private ServicioAdapter servicioAdapter;
    private ProductoAdminAdapter productoAdapter;
    private List<Servicio> serviciosList;
    private List<Producto> productosList;
    private List<Usuario> tecnicosList; // NUEVA LISTA
    private RequestQueue requestQueue;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userName = prefs.getString("name", "Administrador");

        tvWelcome = findViewById(R.id.tvWelcome);
        btnGestionarServicios = findViewById(R.id.btnGestionarServicios);
        btnGestionarInventario = findViewById(R.id.btnGestionarInventario);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        rvServicios = findViewById(R.id.rvServicios);
        rvProductos = findViewById(R.id.rvProductos);

        tvWelcome.setText("Bienvenido, " + userName);

        requestQueue = Volley.newRequestQueue(this);
        serviciosList = new ArrayList<>();
        productosList = new ArrayList<>();
        tecnicosList = new ArrayList<>();

        setupRecyclerViews();
        cargarTecnicos(); // CARGAR TÉCNICOS PRIMERO
        cargarServicios();
        cargarProductos();

        btnGestionarServicios.setOnClickListener(v -> {
            rvServicios.setVisibility(RecyclerView.VISIBLE);
            rvProductos.setVisibility(RecyclerView.GONE);
            cargarServicios();
        });

        btnGestionarInventario.setOnClickListener(v -> {
            rvServicios.setVisibility(RecyclerView.GONE);
            rvProductos.setVisibility(RecyclerView.VISIBLE);
            cargarProductos();
        });

        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupRecyclerViews() {
        // RecyclerView Servicios
        servicioAdapter = new ServicioAdapter(this, serviciosList, servicio -> {
            mostrarDialogoActualizarServicio(servicio);
        });
        rvServicios.setLayoutManager(new LinearLayoutManager(this));
        rvServicios.setAdapter(servicioAdapter);

        // RecyclerView Productos
        productoAdapter = new ProductoAdminAdapter(this, productosList, producto -> {
            mostrarDialogoActualizarStock(producto);
        });
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        rvProductos.setAdapter(productoAdapter);
        rvProductos.setVisibility(RecyclerView.GONE);
    }

    private void cargarServicios() {
        String url = ApiConfig.GET_SERVICIOS + "?role=admin";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("servicios");
                            serviciosList.clear();

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
                                serviciosList.add(servicio);
                            }

                            servicioAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AdminActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AdminActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    // NUEVO: Cargar lista de técnicos
    private void cargarTecnicos() {
        String url = ApiConfig.GET_TECNICOS;
        android.util.Log.d("AdminActivity", "Cargando técnicos desde: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    android.util.Log.d("AdminActivity", "Respuesta técnicos: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("tecnicos");
                            tecnicosList.clear();

                            android.util.Log.d("AdminActivity", "Técnicos encontrados: " + jsonArray.length());

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Usuario tecnico = new Usuario();
                                tecnico.setId(obj.getInt("id"));
                                tecnico.setName(obj.getString("name"));
                                tecnico.setEmail(obj.getString("email"));
                                tecnicosList.add(tecnico);
                                android.util.Log.d("AdminActivity", "Técnico agregado: " + tecnico.getName());
                            }

                            Toast.makeText(AdminActivity.this, "✅ " + tecnicosList.size() + " técnico(s) cargado(s)", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminActivity.this, "❌ Error: " + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AdminActivity.this, "Error cargando técnicos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        android.util.Log.e("AdminActivity", "Error parsing técnicos", e);
                    }
                },
                error -> {
                    String errorMsg = error.getMessage() != null ? error.getMessage() : "Error desconocido";
                    Toast.makeText(AdminActivity.this, "Error de conexión: " + errorMsg, Toast.LENGTH_LONG).show();
                    android.util.Log.e("AdminActivity", "Error de conexión técnicos", error);
                }
        );

        requestQueue.add(request);
    }

    private void cargarProductos() {
        StringRequest request = new StringRequest(Request.Method.GET, ApiConfig.GET_PRODUCTOS,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("productos");
                            productosList.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Producto producto = new Producto();
                                producto.setId(obj.getInt("id"));
                                producto.setNombre(obj.getString("nombre"));
                                producto.setCapacidad(obj.getString("capacidad"));
                                producto.setPrecio(obj.getDouble("precio"));
                                producto.setStock(obj.getInt("stock"));
                                producto.setCategoria(obj.getString("categoria"));
                                producto.setImagenUrl(obj.getString("imagen_url"));
                                productosList.add(producto);
                            }

                            productoAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AdminActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AdminActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    // MEJORADO: Diálogo para asignar técnico
    private void mostrarDialogoActualizarServicio(Servicio servicio) {
        if (servicio.getEstado().equals("Pendiente")) {
            if (tecnicosList.isEmpty()) {
                Toast.makeText(this, "⚠️ No hay técnicos disponibles", Toast.LENGTH_LONG).show();
                cargarTecnicos();
                return;
            }

            // Si solo hay un técnico, asignar automáticamente
            if (tecnicosList.size() == 1) {
                int tecnicoId = tecnicosList.get(0).getId();
                String tecnicoNombre = tecnicosList.get(0).getName();

                new AlertDialog.Builder(this)
                        .setTitle("Asignar Servicio")
                        .setMessage("¿Asignar este servicio a " + tecnicoNombre + "?\n\n" +
                                "Cliente: " + servicio.getClientName() +
                                "\nExtintor: " + servicio.getTipo() + " - " + servicio.getCapacidad() +
                                "\nUbicación: " + servicio.getUbicacion())
                        .setPositiveButton("✅ ASIGNAR", (d, w) -> {
                            actualizarServicio(servicio.getId(), "Asignado", tecnicoId);
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return;
            }

            // Si hay varios técnicos, mostrar lista para elegir
            String[] nombresTecnicos = new String[tecnicosList.size()];
            for (int i = 0; i < tecnicosList.size(); i++) {
                nombresTecnicos[i] = tecnicosList.get(i).getName();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Seleccionar Técnico")
                    .setMessage("Cliente: " + servicio.getClientName() +
                            "\nExtintor: " + servicio.getTipo() + " - " + servicio.getCapacidad())
                    .setItems(nombresTecnicos, (dialog, which) -> {
                        int tecnicoId = tecnicosList.get(which).getId();
                        actualizarServicio(servicio.getId(), "Asignado", tecnicoId);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else if (servicio.getEstado().equals("Asignado")) {
            Toast.makeText(this, "✅ Servicio asignado a: " + servicio.getTecnicoName() + "\n⏳ Esperando que el técnico inicie", Toast.LENGTH_LONG).show();
        } else if (servicio.getEstado().equals("En Proceso")) {
            Toast.makeText(this, "🔧 Servicio en proceso\nTécnico: " + servicio.getTecnicoName(), Toast.LENGTH_LONG).show();
        } else if (servicio.getEstado().equals("Completado")) {
            Toast.makeText(this, "🎉 Servicio completado por: " + servicio.getTecnicoName(), Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarServicio(int servicioId, String estado, int tecnicoId) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.ACTUALIZAR_SERVICIO,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Toast.makeText(AdminActivity.this, "✅ " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            cargarServicios();
                        } else {
                            Toast.makeText(AdminActivity.this, "❌ Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AdminActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AdminActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("servicio_id", String.valueOf(servicioId));
                params.put("estado", estado);
                params.put("tecnico_id", String.valueOf(tecnicoId));
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void mostrarDialogoActualizarStock(Producto producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Stock: " + producto.getNombre());

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(producto.getStock()));
        builder.setView(input);

        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            int nuevoStock = Integer.parseInt(input.getText().toString());
            actualizarStock(producto.getId(), nuevoStock);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void actualizarStock(int productoId, int stock) {
        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.ACTUALIZAR_STOCK,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(AdminActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        cargarProductos();
                    } catch (Exception e) {
                        Toast.makeText(AdminActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AdminActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("producto_id", String.valueOf(productoId));
                params.put("stock", String.valueOf(stock));
                return params;
            }
        };

        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTecnicos();
        cargarServicios();
    }
}