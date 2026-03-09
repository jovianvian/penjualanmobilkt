<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['success' => false, 'message' => ''];
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $kode = $_POST['kode_cash'] ?? '';
    $ktp = $_POST['ktp'] ?? '';
    $kodeMobil = $_POST['kode_mobil'] ?? '';
    $tanggal = $_POST['cash_tgl'] ?? '';
    $bayar = $_POST['cash_bayar'] ?? '';

    if ($kode !== '' && $ktp !== '' && $kodeMobil !== '' && $tanggal !== '' && $bayar !== '') {
        $stmt = $conn->prepare("INSERT INTO beli_cash (kode_cash, ktp, kode_mobil, cash_tgl, cash_bayar) VALUES (?, ?, ?, ?, ?)");
        $stmt->bind_param("ssssi", $kode, $ktp, $kodeMobil, $tanggal, $bayar);
        $response['success'] = $stmt->execute();
        $response['message'] = $response['success'] ? 'Data cash berhasil ditambahkan' : ('Gagal menambah data: ' . $stmt->error);
        $stmt->close();
    } else {
        $response['message'] = 'Semua data wajib diisi';
    }
}
echo json_encode($response);
?>
