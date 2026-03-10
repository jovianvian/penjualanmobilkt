<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$response = ['success' => false, 'message' => ''];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $ktp = $_POST['ktp'] ?? '';

    if ($ktp) {
        $stmt = $conn->prepare("DELETE FROM pembeli WHERE ktp=?");
        $stmt->bind_param("s", $ktp);

        if ($stmt->execute()) {
            $response['success'] = true;
            $response['message'] = 'Data berhasil dihapus';
        } else {
            $response['message'] = 'Gagal hapus data: ' . $stmt->error;
        }

        $stmt->close();
    } else {
        $response['message'] = 'KTP wajib diisi!';
    }
} else {
    $response['message'] = 'Metode request tidak valid!';
}

echo json_encode($response);
?>