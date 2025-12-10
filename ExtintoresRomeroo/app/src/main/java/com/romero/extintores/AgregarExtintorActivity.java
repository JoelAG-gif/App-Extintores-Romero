package com.romero.extintores;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AgregarExtintorActivity extends AppCompatActivity {
    private RecyclerView rvProductos;
    private ProductoAdapter adapter;
    private List<Producto> productosList;
    private EditText etUbicacion, etCantidad;
    private Spinner spinnerVencimiento;
    private RadioGroup radioGroupPago;
    private RadioButton rbEfectivo, rbTarjeta, rbTransferencia;
    private TextView tvResumen;
    private Button btnConfirmarCompra;
    private RequestQueue requestQueue;
    private int userId;
    private Producto productoSeleccionado;
    private List<String> fechasVencimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_extintor);

        try {
            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
            userId = prefs.getInt("user_id", 0);

            // Inicializar vistas
            rvProductos = findViewById(R.id.rvProductos);
            etUbicacion = findViewById(R.id.etUbicacion);
            etCantidad = findViewById(R.id.etCantidad);
            spinnerVencimiento = findViewById(R.id.spinnerVencimiento);
            radioGroupPago = findViewById(R.id.radioGroupPago);
            rbEfectivo = findViewById(R.id.rbEfectivo);
            rbTarjeta = findViewById(R.id.rbTarjeta);
            rbTransferencia = findViewById(R.id.rbTransferencia);
            tvResumen = findViewById(R.id.tvResumen);
            btnConfirmarCompra = findViewById(R.id.btnConfirmarCompra);

            requestQueue = Volley.newRequestQueue(this);
            productosList = new ArrayList<>();
            fechasVencimiento = new ArrayList<>();

            configurarFechasVencimiento();
            setupRecyclerView();
            cargarProductos();

            // Listener para actualizar resumen cuando cambie la cantidad
            etCantidad.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    actualizarResumen();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            btnConfirmarCompra.setOnClickListener(v -> agregarExtintor());

        } catch (Exception e) {
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void configurarFechasVencimiento() {
        fechasVencimiento.clear();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Generar fechas de vencimiento desde 6 meses hasta 5 años
        String[] opciones = {
                "6 meses", "1 año", "2 años", "3 años", "4 años", "5 años"
        };
        int[] mesesAgregar = {6, 12, 24, 36, 48, 60};

        List<String> opcionesDisplay = new ArrayList<>();

        for (int i = 0; i < opciones.length; i++) {
            Calendar tempCal = (Calendar) calendar.clone();
            tempCal.add(Calendar.MONTH, mesesAgregar[i]);
            String fecha = sdf.format(tempCal.getTime());
            fechasVencimiento.add(fecha);
            opcionesDisplay.add(opciones[i] + " (" + fecha + ")");
        }

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opcionesDisplay);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVencimiento.setAdapter(adapterSpinner);
        spinnerVencimiento.setSelection(2); // 2 años por defecto
    }

    private void setupRecyclerView() {
        adapter = new ProductoAdapter(this, productosList, producto -> {
            productoSeleccionado = producto;
            actualizarResumen();
            Toast.makeText(this, "✅ Seleccionado: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
        });
        rvProductos.setLayoutManager(new GridLayoutManager(this, 2));
        rvProductos.setAdapter(adapter);
    }

    private void actualizarResumen() {
        if (productoSeleccionado == null) {
            tvResumen.setText("Selecciona un producto para ver el resumen");
            return;
        }

        String cantidadStr = etCantidad.getText().toString().trim();
        int cantidad = cantidadStr.isEmpty() ? 1 : Integer.parseInt(cantidadStr);
        double total = productoSeleccionado.getPrecio() * cantidad;

        String resumen = "Producto: " + productoSeleccionado.getNombre() + "\n" +
                "Precio unitario: S/ " + String.format("%.2f", productoSeleccionado.getPrecio()) + "\n" +
                "Cantidad: " + cantidad + "\n" +
                "TOTAL: S/ " + String.format("%.2f", total);

        tvResumen.setText(resumen);
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

                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AgregarExtintorActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AgregarExtintorActivity.this, "Error de conexión: " + error.toString(), Toast.LENGTH_LONG).show()
        );

        requestQueue.add(request);
    }

    private void agregarExtintor() {
        if (productoSeleccionado == null) {
            Toast.makeText(this, "❌ Selecciona un producto", Toast.LENGTH_SHORT).show();
            return;
        }

        String ubicacion = etUbicacion.getText().toString().trim();
        String cantidadStr = etCantidad.getText().toString().trim();

        if (ubicacion.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "❌ Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "❌ Cantidad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        int posicionVencimiento = spinnerVencimiento.getSelectedItemPosition();
        String fechaVencimiento = fechasVencimiento.get(posicionVencimiento);

        // Obtener método de pago seleccionado
        String metodoPago = "Efectivo";
        int selectedId = radioGroupPago.getCheckedRadioButtonId();

        if (selectedId == R.id.rbTarjeta) {
            metodoPago = "Tarjeta";
        } else if (selectedId == R.id.rbTransferencia) {
            metodoPago = "Transferencia";
        }

        if (cantidad > productoSeleccionado.getStock()) {
            Toast.makeText(this, "❌ Stock insuficiente. Disponible: " + productoSeleccionado.getStock(), Toast.LENGTH_SHORT).show();
            return;
        }

        final String metodoPagoFinal = metodoPago;

        StringRequest request = new StringRequest(Request.Method.POST, ApiConfig.ADD_EXTINTOR,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Toast.makeText(AgregarExtintorActivity.this, "✅ " + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            // Limpiar campos
                            etUbicacion.setText("");
                            etCantidad.setText("1");
                            productoSeleccionado = null;
                            tvResumen.setText("Selecciona un producto para ver el resumen");
                            cargarProductos(); // Recargar para actualizar stock
                            Toast.makeText(AgregarExtintorActivity.this, "✨ Ve a 'Mis Extintores' para ver tu compra", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AgregarExtintorActivity.this, "❌ " + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(AgregarExtintorActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AgregarExtintorActivity.this, "Error de conexión: " + error.toString(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("producto_id", String.valueOf(productoSeleccionado.getId()));
                params.put("ubicacion", ubicacion);
                params.put("fecha_vencimiento", fechaVencimiento);
                params.put("cantidad", String.valueOf(cantidad));
                params.put("metodo_pago", metodoPagoFinal);
                return params;
            }
        };

        requestQueue.add(request);
    }
}