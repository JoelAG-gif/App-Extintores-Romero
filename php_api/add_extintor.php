<?php
require_once 'config.php';

$user_id = $_POST['user_id'] ?? '';
$producto_id = $_POST['producto_id'] ?? '';
$ubicacion = $_POST['ubicacion'] ?? '';
$fecha_vencimiento = $_POST['fecha_vencimiento'] ?? '';
$cantidad = $_POST['cantidad'] ?? 1;
$metodo_pago = $_POST['metodo_pago'] ?? 'Efectivo';

if(empty($user_id) || empty($producto_id) || empty($fecha_vencimiento)) {
    echo json_encode(['success' => false, 'message' => 'Faltan datos obligatorios']);
    exit();
}

try {
    $conn->beginTransaction();
    
    // Verificar stock disponible
    $stmt = $conn->prepare("SELECT nombre, capacidad, stock, precio FROM productos WHERE id = ?");
    $stmt->execute([$producto_id]);
    $producto = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if(!$producto) {
        echo json_encode(['success' => false, 'message' => 'Producto no encontrado']);
        exit();
    }
    
    if($producto['stock'] < $cantidad) {
        echo json_encode(['success' => false, 'message' => 'Stock insuficiente. Disponible: ' . $producto['stock']]);
        exit();
    }
    
    // Agregar extintor al cliente
    $stmt = $conn->prepare("INSERT INTO extintores (user_id, producto_id, tipo, capacidad, ubicacion, fecha_vencimiento) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->execute([$user_id, $producto_id, $producto['nombre'], $producto['capacidad'], $ubicacion, $fecha_vencimiento]);
    
    $extintor_id = $conn->lastInsertId();
    
    // Actualizar stock
    $stmt = $conn->prepare("UPDATE productos SET stock = stock - ? WHERE id = ?");
    $stmt->execute([$cantidad, $producto_id]);
    
    // Registrar venta
    $monto_total = $producto['precio'] * $cantidad;
    $stmt = $conn->prepare("INSERT INTO ventas (user_id, producto_id, cantidad, monto_total, metodo_pago) VALUES (?, ?, ?, ?, ?)");
    $stmt->execute([$user_id, $producto_id, $cantidad, $monto_total, $metodo_pago]);
    
    $conn->commit();
    
    echo json_encode([
        'success' => true,
        'message' => 'Extintor agregado exitosamente',
        'extintor_id' => $extintor_id,
        'stock_restante' => $producto['stock'] - $cantidad
    ]);
} catch(PDOException $e) {
    $conn->rollBack();
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>