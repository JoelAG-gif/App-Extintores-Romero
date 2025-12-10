<?php
require_once 'config.php';

$role = $_GET['role'] ?? '';
$user_id = $_GET['user_id'] ?? '';

try {
    if($role == 'admin' || $role == 'tecnico') {
        // Admin y técnico ven todas las solicitudes
        $stmt = $conn->query("
            SELECT s.*, 
            u.name as client_name, u.email as client_email,
            e.tipo, e.capacidad, e.ubicacion,
            t.name as tecnico_name
            FROM servicios s
            JOIN users u ON s.client_id = u.id
            JOIN extintores e ON s.extintor_id = e.id
            LEFT JOIN users t ON s.tecnico_id = t.id
            ORDER BY s.fecha_solicitud DESC
        ");
    } else {
        // Cliente solo ve sus solicitudes
        $stmt = $conn->prepare("
            SELECT s.*, 
            e.tipo, e.capacidad, e.ubicacion,
            t.name as tecnico_name
            FROM servicios s
            JOIN extintores e ON s.extintor_id = e.id
            LEFT JOIN users t ON s.tecnico_id = t.id
            WHERE s.client_id = ?
            ORDER BY s.fecha_solicitud DESC
        ");
        $stmt->execute([$user_id]);
    }
    
    $servicios = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode([
        'success' => true,
        'servicios' => $servicios
    ]);
} catch(PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>