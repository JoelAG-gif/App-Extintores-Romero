<?php
require_once 'config.php';

$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

if(empty($email) || empty($password)) {
    echo json_encode(['success' => false, 'message' => 'Email y contraseña son requeridos']);
    exit();
}

try {
    $stmt = $conn->prepare("SELECT id, name, email, role FROM users WHERE email = ? AND password = ?");
    $stmt->execute([$email, $password]);
    
    $user = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if($user) {
        echo json_encode([
            'success' => true,
            'message' => 'Login exitoso',
            'user' => $user
        ]);
    } else {
        echo json_encode(['success' => false, 'message' => 'Credenciales incorrectas']);
    }
} catch(PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $e->getMessage()]);
}
?>