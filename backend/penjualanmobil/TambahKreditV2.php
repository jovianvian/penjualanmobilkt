<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['success' => false, 'message' => ''];
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $kode = $_POST['kode_kredit'] ?? '';
    $ktp = $_POST['ktp'] ?? '';
    $kodePaket = $_POST['kode_paket'] ?? '';
    $kodeMobil = $_POST['kode_mobil'] ?? '';
    $tanggal = $_POST['tanggal_kredit'] ?? '';
    $bayar = $_POST['bayar_kredit'] ?? '';
    $tenor = $_POST['tenor'] ?? '';
    $total = $_POST['totalcicil'] ?? '';

    if ($kode !== '' && $ktp !== '' && $kodePaket !== '' && $kodeMobil !== '' && $tanggal !== '' && $bayar !== '' && $tenor !== '' && $total !== '') {
        $stmt = $conn->prepare("INSERT INTO kredit (kode_kredit, ktp, kode_paket, kode_mobil, tanggal_kredit, bayar_kredit, tenor, totalcicil) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("sssssiii", $kode, $ktp, $kodePaket, $kodeMobil, $tanggal, $bayar, $tenor, $total);
        $response['success'] = $stmt->execute();
        $response['message'] = $response['success'] ? 'Data kredit berhasil ditambahkan' : ('Gagal menambah data: ' . $stmt->error);
        $stmt->close();
    } else {
        $response['message'] = 'Semua data wajib diisi';
    }
}
echo json_encode($response);
?>
