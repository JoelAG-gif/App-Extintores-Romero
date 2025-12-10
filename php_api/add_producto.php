<?php
require_once 'config.php';

$nombre = $_POST['nombre'] ?? '';
$capacidad = $_POST['capacidad'] ?? '';
$precio = $_POST['precio'] ?? 0;
$stock = $_POST['stock'] ?? 0;
$categoria = $_POST['categoria'] ?? 'Producto';
$imagen_url = $_POST['imagen_url'] ?? 'default_extintor.jpg';

if(empty($nombre) || empty($precio)) {
    echo json_encode(['success' => false, 'message' => 'Nombre y precio son requeridos']);
    exit();
}

try {
    $stmt = $conn->prepare("INSERT INTO productos (nombre, capacidad, precio, stock, categoria, imagen_url) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->execute([$nombre, $capacidad, $precio, $stock, $categoria, $imagen_url]);
    
    echo json_encode([
        'success' => true,
        'message' => 'Producto agregado exitosamente',
        'producto_id' => $conn->lastInsertId()
    ]);
} catch(PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>