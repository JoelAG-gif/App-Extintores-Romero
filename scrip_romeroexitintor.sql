DROP DATABASE IF EXISTS extintores_db;
CREATE DATABASE extintores_db;
USE extintores_db;

-- 1. USUARIOS
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, 
    role ENUM('admin', 'tecnico', 'client') DEFAULT 'client',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. PRODUCTOS
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    capacidad VARCHAR(50),
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    categoria ENUM('Producto', 'Repuesto', 'Insumo') DEFAULT 'Producto',
    imagen_url VARCHAR(255) DEFAULT 'default_extintor.jpg'
);

-- 3. EXTINTORES DEL CLIENTE
CREATE TABLE extintores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    producto_id INT NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    capacidad VARCHAR(50) NOT NULL,
    ubicacion VARCHAR(100),
    fecha_vencimiento DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- 4. SERVICIOS 
CREATE TABLE servicios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    extintor_id INT NOT NULL,
    producto_id INT NOT NULL,
    tecnico_id INT NULL,
    estado ENUM('Pendiente', 'Asignado', 'Completado') DEFAULT 'Pendiente',
    fecha_solicitud DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_servicio DATETIME NULL,
    descripcion TEXT,
    fecha_completado DATETIME NULL,
    direccion_servicio VARCHAR(255),
    FOREIGN KEY (client_id) REFERENCES users(id),
    FOREIGN KEY (extintor_id) REFERENCES extintores(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    FOREIGN KEY (tecnico_id) REFERENCES users(id)
);

-- 5. VENTAS
CREATE TABLE ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT DEFAULT 1,
    monto_total DECIMAL(10,2),
    metodo_pago ENUM('Efectivo', 'Tarjeta', 'Transferencia') DEFAULT 'Efectivo',
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

INSERT INTO users (name, email, password, role) VALUES 
('Administrador', 'admin@romero.com', '123456', 'admin'),
('Tecnico Jireh', 'tecnico@romero.com', '123456', 'tecnico');

INSERT INTO productos (nombre, capacidad, precio, stock, categoria, imagen_url) VALUES 
('Extintor PQS ABC', '6kg', 120.00, 10, 'Producto', 'extintor_pqs.jpg'),
('Extintor CO2', '10kg', 250.00, 5, 'Producto', 'extintor_co2.jpg'),
('Extintor Agua', '9L', 100.00, 0, 'Producto', 'extintor_agua.jpg'),
('Extintor Espuma', '6L', 180.00, 8, 'Producto', 'extintor_espuma.jpg'),
('Válvula Premium', 'N/A', 25.00, 50, 'Repuesto', 'valvula.jpg');

ALTER TABLE servicios 
MODIFY COLUMN estado ENUM('Pendiente', 'Asignado', 'En Proceso', 'Completado') DEFAULT 'Pendiente';
SELECT * FROM users WHERE role = 'tecnico';
