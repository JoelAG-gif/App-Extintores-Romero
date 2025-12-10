<?php
require_once 'config.php';

try {
    $stmt = $conn->query("SELECT id, name, email FROM users WHERE role = 'tecnico' ORDER BY name ASC");
    $tecnicos = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'tecnicos' => $tecnicos
    ]);
} catch(PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>