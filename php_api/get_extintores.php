<?php
require_once 'config.php';

$user_id = $_GET['user_id'] ?? '';

if(empty($user_id)) {
    echo json_encode(['success' => false, 'message' => 'ID de usuario requerido']);
    exit();
}

try {
    $stmt = $conn->prepare("
        SELECT e.*, p.imagen_url,
        DATEDIFF(e.fecha_vencimiento, CURDATE()) as dias_restantes
        FROM extintores e
        LEFT JOIN productos p ON e.producto_id = p.id
        WHERE e.user_id = ?
        ORDER BY e.fecha_vencimiento ASC
    ");
    $stmt->execute([$user_id]);
    $extintores = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // Marcar alertas (vencimiento en 30 días o menos)
    foreach($extintores as &$extintor) {
        $extintor['alerta'] = ($extintor['dias_restantes'] <= 30 && $extintor['dias_restantes'] >= 0);
        $extintor['vencido'] = ($extintor['dias_restantes'] < 0);
    }
    
    echo json_encode([
        'success' => true,
        'extintores' => $extintores
    ]);
} catch(PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>