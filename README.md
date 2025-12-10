# 📱 App Móvil - Gestión de Extintores (Extintores Romero)

Aplicación nativa para Android desarrollada para optimizar la gestión de ventas, servicios y clientes de una empresa de extintores.

## 🎥 Demo de la Aplicación

### (https://drive.google.com/file/d/1xybv6MSLWf7iS80w5ca8MGPkGPYVXFwW/view?usp=sharing)

---

## 🛠️ Tecnologías Utilizadas

### Frontend (Móvil)
* **Lenguaje:** Java (Android Studio)
* **UI:** XML Layouts
* **Conexión:** Retrofit / Volley para consumo de API REST

### Backend (API)
* **Lenguaje:** PHP (Vanilla)
* **Servidor:** Apache (XAMPP / Hosting compartido)
* **Formato:** JSON

### Base de Datos
* **Motor:** MySQL
* **Diseño:** Relacional (Clientes, Pedidos, Inventario)

---

## 📂 Estructura del Proyecto

Este repositorio contiene todo el código fuente necesario para desplegar el sistema:

* 📂 **`ExtintoresRomeroo`**: Código fuente de la aplicación Android (Android Studio Project).
* 📂 **`php_api`**: Archivos PHP que conforman la API REST (Login, CRUD de productos, etc.).
* 📄 **`scrip_romeroexitintor.sql`**: Script SQL para crear e importar la base de datos MySQL.

## 🚀 Instalación y Despliegue

1.  **Base de Datos:** Importa el archivo `.sql` en tu gestor de base de datos (phpMyAdmin o Workbench).
2.  **Backend:** Sube la carpeta `php_api` a tu servidor local (`htdocs`) o hosting web.
3.  **App Android:**
    * Abre la carpeta `ExtintoresRomeroo` en Android Studio.
    * Busca el archivo de configuración de API (usualmente en `Constants.java` o `ApiInterface.java`) y actualiza la IP/URL de tu servidor.
    * Ejecuta el proyecto en un emulador o dispositivo físico.

---
Hecho por **[Joel Aroni]** - 2025
