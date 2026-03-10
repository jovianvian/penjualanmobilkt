<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['success' => false, 'message' => ''];
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $kode = $_POST['kode_cicilan'] ?? '';
    $kodeKredit = $_POST['kode_kredit'] ?? '';
    $tanggal = $_POST['tanggal_cicilan'] ?? '';
    $ke = $_POST['cicilanke'] ?? '';
    $jumlah = $_POST['jumlah_cicilan'] ?? '';
    $sisaKe = $_POST['sisacicilke'] ?? '';
    $sisa = $_POST['sisa_cicilan'] ?? '';

    if ($kode !== '' && $kodeKredit !== '' && $tanggal !== '' && $ke !== '' && $jumlah !== '' && $sisaKe !== '' && $sisa !== '') {
        $stmt = $conn->prepare("INSERT INTO bayar_cicilan (kode_cicilan, kode_kredit, tanggal_cicilan, cicilanke, jumlah_cicilan, sisacicilke, sisa_cicilan) VALUES (?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("sssiiii", $kode, $kodeKredit, $tanggal, $ke, $jumlah, $sisaKe, $sisa);
        $response['success'] = $stmt->execute();
        $response['message'] = $response['success'] ? 'Data cicilan berhasil ditambahkan' : ('Gagal menambah data: ' . $stmt->error);
        $stmt->close();
    } else {
        $response['message'] = 'Semua data wajib diisi';
    }
}
echo json_encode($response);
?>
