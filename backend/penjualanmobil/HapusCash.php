<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['status' => 0, 'message' => 'Gagal menghapus'];
$kode = $_POST['kode_cash'] ?? '';
if ($kode !== '') {
    $stmt = $conn->prepare("DELETE FROM beli_cash WHERE kode_cash=?");
    $stmt->bind_param("s", $kode);
    if ($stmt->execute()) {
        $response = ['status' => 1, 'message' => 'Data cash berhasil dihapus'];
    } else {
        $response = ['status' => 0, 'message' => 'Hapus gagal: ' . $stmt->error];
    }
    $stmt->close();
}
echo json_encode($response);
?>
