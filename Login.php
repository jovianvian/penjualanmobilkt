<?php
include 'Koneksi.php';

$username = $_POST['username'];
$password = $_POST['password'];

$sql = "SELECT * FROM user WHERE username='$username' AND password='$password'";
$result = mysqli_query($conn, $sql);

$response = array();

if (mysqli_num_rows($result) > 0) {
    $row = mysqli_fetch_assoc($result);
    $response['success'] = true;
    $response['level'] = $row['level'];
    $response['message'] = "Login berhasil";
} else {
    $response['success'] = false;
    $response['message'] = "Username atau password salah";
}

echo json_encode($response);
?>
