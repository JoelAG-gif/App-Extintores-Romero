<?php
require_once 'config.php';

$producto_id = $_POST['producto_id'] ?? '';
$nuevo_stock = $_POST['stock'] ?? 0;

if(empty($producto_id)) {
    echo json_encode(['success' => false, 'message' => 'ID de producto requerido']);
    exit();
}

try {
    $stmt = $conn->prepare("UPDATE productos SET stock = ? WHERE id = ?");
    $stmt->execute([$nuevo_stock, $producto_id]);
    
    echo json_encode(['success' => true, 'message' => 'Stock actualizado']);
} catch(PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>