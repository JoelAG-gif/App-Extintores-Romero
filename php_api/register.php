<?php
require_once 'config.php';

$name = $_POST['name'] ?? '';
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

if(empty($name) || empty($email) || empty($password)) {
    echo json_encode(['success' => false, 'message' => 'Todos los campos son requeridos']);
    exit();
}

try {
    // Verificar si el email ya existe
    $stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->execute([$email]);
    
    if($stmt->fetch()) {
        echo json_encode(['success' => false, 'message' => 'El email ya está registrado']);
        exit();
    }
    
    // Registrar nuevo cliente
    $stmt = $conn->prepare("INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, 'client')");
    $stmt->execute([$name, $email, $password]);
    
    echo json_encode(['success' => true, 'message' => 'Registro exitoso']);
} catch(PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>