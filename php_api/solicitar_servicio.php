<?php
require_once 'config.php';

$client_id = $_POST['client_id'] ?? '';
$extintor_id = $_POST['extintor_id'] ?? '';
$fecha_servicio = $_POST['fecha_servicio'] ?? '';
$descripcion = $_POST['descripcion'] ?? '';

if(empty($client_id) || empty($extintor_id) || empty($fecha_servicio)) {
    echo json_encode(['success' => false, 'message' => 'Faltan datos obligatorios']);
    exit();
}

try {
    // PRIMERO: Obtener el producto_id del extintor seleccionado
    $stmt = $conn->prepare("SELECT producto_id, ubicacion FROM extintores WHERE id = ?");
    $stmt->execute([$extintor_id]);
    $extintor = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if(!$extintor) {
        echo json_encode(['success' => false, 'message' => 'Extintor no encontrado']);
        exit();
    }
    
    $producto_id = $extintor['producto_id'];
    $direccion_servicio = $extintor['ubicacion'];
    
    // AHORA SÍ: Insertar el servicio con producto_id incluido
    $stmt = $conn->prepare("INSERT INTO servicios (client_id, extintor_id, producto_id, fecha_servicio, descripcion, direccion_servicio) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->execute([$client_id, $extintor_id, $producto_id, $fecha_servicio, $descripcion, $direccion_servicio]);
    
    echo json_encode([
        'success' => true,
        'message' => 'Solicitud de servicio enviada. El administrador y técnico serán notificados.',
        'servicio_id' => $conn->lastInsertId()
    ]);
} catch(PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>