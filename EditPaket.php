<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['status' => 0, 'message' => 'Data tidak lengkap'];
$kode = $_POST['kode_paket'] ?? '';
$uangMuka = $_POST['uang_muka'] ?? '';
$tenor = $_POST['tenor'] ?? '';
$bunga = $_POST['bunga_cicilan'] ?? '';

if ($kode !== '' && $uangMuka !== '' && $tenor !== '' && $bunga !== '') {
    $stmt = $conn->prepare("UPDATE paket SET uang_muka=?, tenor=?, bunga_cicilan=? WHERE kode_paket=?");
    $stmt->bind_param("iiis", $uangMuka, $tenor, $bunga, $kode);
    if ($stmt->execute()) {
        $response = ['status' => 1, 'message' => 'Data paket berhasil diupdate'];
    } else {
        $response = ['status' => 0, 'message' => 'Update gagal: ' . $stmt->error];
    }
    $stmt->close();
}
echo json_encode($response);
?>
