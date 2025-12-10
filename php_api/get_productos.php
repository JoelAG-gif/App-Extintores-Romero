<?php
require_once 'config.php';

try {
    $stmt = $conn->query("SELECT * FROM productos ORDER BY nombre ASC");
    $productos = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'productos' => $productos
    ]);
} catch(PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>