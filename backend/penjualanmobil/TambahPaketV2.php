<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['success' => false, 'message' => ''];
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $kode = $_POST['kode_paket'] ?? '';
    $uangMuka = $_POST['uang_muka'] ?? '';
    $tenor = $_POST['tenor'] ?? '';
    $bunga = $_POST['bunga_cicilan'] ?? '';

    if ($kode !== '' && $uangMuka !== '' && $tenor !== '' && $bunga !== '') {
        $stmt = $conn->prepare("INSERT INTO paket (kode_paket, uang_muka, tenor, bunga_cicilan) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("siii", $kode, $uangMuka, $tenor, $bunga);
        $response['success'] = $stmt->execute();
        $response['message'] = $response['success'] ? 'Data paket berhasil ditambahkan' : ('Gagal menambah data: ' . $stmt->error);
        $stmt->close();
    } else {
        $response['message'] = 'Semua data wajib diisi';
    }
}
echo json_encode($response);
?>
