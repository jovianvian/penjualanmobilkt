<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['status' => 0, 'message' => 'Gagal menghapus'];
$kode = $_POST['kode_kredit'] ?? '';
if ($kode !== '') {
    $stmt = $conn->prepare("DELETE FROM kredit WHERE kode_kredit=?");
    $stmt->bind_param("s", $kode);
    if ($stmt->execute()) {
        $response = ['status' => 1, 'message' => 'Data kredit berhasil dihapus'];
    } else {
        $response = ['status' => 0, 'message' => 'Hapus gagal: ' . $stmt->error];
    }
    $stmt->close();
}
echo json_encode($response);
?>
