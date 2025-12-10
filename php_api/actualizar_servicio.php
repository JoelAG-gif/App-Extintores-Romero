<?php
require_once 'config.php';

$servicio_id = $_POST['servicio_id'] ?? '';
$tecnico_id = $_POST['tecnico_id'] ?? null;
$estado = $_POST['estado'] ?? '';

if(empty($servicio_id) || empty($estado)) {
    echo json_encode(['success' => false, 'message' => 'Faltan datos']);
    exit();
}

try {
    // Determinar qué campos actualizar según el estado
    if($estado == 'Completado') {
        // Al completar, registrar fecha de completado
        $stmt = $conn->prepare("UPDATE servicios SET estado = ?, fecha_completado = NOW() WHERE id = ?");
        $stmt->execute([$estado, $servicio_id]);
    } else if($estado == 'Asignado') {
        // Al asignar, guardar el técnico
        if(empty($tecnico_id)) {
            echo json_encode(['success' => false, 'message' => 'Debe seleccionar un técnico']);
            exit();
        }
        $stmt = $conn->prepare("UPDATE servicios SET estado = ?, tecnico_id = ? WHERE id = ?");
        $stmt->execute([$estado, $tecnico_id, $servicio_id]);
    } else {
        // Para "En Proceso" u otros estados
        $stmt = $conn->prepare("UPDATE servicios SET estado = ? WHERE id = ?");
        $stmt->execute([$estado, $servicio_id]);
    }
    
    echo json_encode(['success' => true, 'message' => 'Servicio actualizado correctamente']);
} catch(PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>