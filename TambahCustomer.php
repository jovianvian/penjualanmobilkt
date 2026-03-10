<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$response = ['success' => false, 'message' => ''];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $nama  = $_POST['Nama_Customer'] ?? '';
    $email = $_POST['Email'] ?? '';
    $telp  = $_POST['NoTelp'] ?? '';

    if ($nama && $email && $telp) {

            $stmt = $conn->prepare("INSERT INTO customer (Nama_Customer, Email, NoTelp) VALUES (?,?,?)");
            $stmt->bind_param("ssi", $nama, $email, $telp);

            $response['success'] = $stmt->execute();
            $response['message'] = $response['success']
                ? "Data berhasil disimpan"
                : "Gagal menyimpan data: " . $stmt->error;

            $stmt->close();
    } else {
        $response['message'] = "Semua data harus diisi!";
    }
} else {
    $response['message'] = "Metode request tidak valid!";
}

echo json_encode($response);
?>
