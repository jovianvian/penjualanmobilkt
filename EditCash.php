<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['status' => 0, 'message' => 'Data tidak lengkap'];
$kode = $_POST['kode_cash'] ?? '';
$ktp = $_POST['ktp'] ?? '';
$kodeMobil = $_POST['kode_mobil'] ?? '';
$tanggal = $_POST['cash_tgl'] ?? '';
$bayar = $_POST['cash_bayar'] ?? '';

if ($kode !== '' && $ktp !== '' && $kodeMobil !== '' && $tanggal !== '' && $bayar !== '') {
    $stmt = $conn->prepare("UPDATE beli_cash SET ktp=?, kode_mobil=?, cash_tgl=?, cash_bayar=? WHERE kode_cash=?");
    $stmt->bind_param("sssis", $ktp, $kodeMobil, $tanggal, $bayar, $kode);
    if ($stmt->execute()) {
        $response = ['status' => 1, 'message' => 'Data cash berhasil diupdate'];
    } else {
        $response = ['status' => 0, 'message' => 'Update gagal: ' . $stmt->error];
    }
    $stmt->close();
}
echo json_encode($response);
?>
