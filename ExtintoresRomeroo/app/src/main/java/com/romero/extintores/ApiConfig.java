package com.romero.extintores;

public class ApiConfig {

    private static final String BASE_URL = "http://192.168.18.130/php_api/";

    public static final String LOGIN = BASE_URL + "login.php";
    public static final String REGISTER = BASE_URL + "register.php";
    public static final String GET_PRODUCTOS = BASE_URL + "get_productos.php";
    public static final String ADD_EXTINTOR = BASE_URL + "add_extintor.php";
    public static final String GET_EXTINTORES = BASE_URL + "get_extintores.php";
    public static final String SOLICITAR_SERVICIO = BASE_URL + "solicitar_servicio.php";
    public static final String GET_SERVICIOS = BASE_URL + "get_servicios.php";
    public static final String ACTUALIZAR_SERVICIO = BASE_URL + "actualizar_servicio.php";
    public static final String ADD_PRODUCTO = BASE_URL + "add_producto.php";
    public static final String ACTUALIZAR_STOCK = BASE_URL + "actualizar_stock.php";
    public static final String GET_TECNICOS = BASE_URL + "get_tecnicos.php";
    public static final String IMAGE_URL = BASE_URL + "images/";
}