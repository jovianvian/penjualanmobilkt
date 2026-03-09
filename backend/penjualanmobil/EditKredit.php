<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['status' => 0, 'message' => 'Data tidak lengkap'];
$kode = $_POST['kode_kredit'] ?? '';
$ktp = $_POST['ktp'] ?? '';
$kodePaket = $_POST['kode_paket'] ?? '';
$kodeMobil = $_POST['kode_mobil'] ?? '';
$tanggal = $_POST['tanggal_kredit'] ?? '';
$bayar = $_POST['bayar_kredit'] ?? '';
$tenor = $_POST['tenor'] ?? '';
$total = $_POST['totalcicil'] ?? '';

if ($kode !== '' && $ktp !== '' && $kodePaket !== '' && $kodeMobil !== '' && $tanggal !== '' && $bayar !== '' && $tenor !== '' && $total !== '') {
    $stmt = $conn->prepare("UPDATE kredit SET ktp=?, kode_paket=?, kode_mobil=?, tanggal_kredit=?, bayar_kredit=?, tenor=?, totalcicil=? WHERE kode_kredit=?");
    $stmt->bind_param("ssssiiis", $ktp, $kodePaket, $kodeMobil, $tanggal, $bayar, $tenor, $total, $kode);
    if ($stmt->execute()) {
        $response = ['status' => 1, 'message' => 'Data kredit berhasil diupdate'];
    } else {
        $response = ['status' => 0, 'message' => 'Update gagal: ' . $stmt->error];
    }
    $stmt->close();
}
echo json_encode($response);
?>
