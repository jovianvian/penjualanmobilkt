<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['status' => 0, 'message' => 'Data tidak lengkap'];
$kode = $_POST['kode_cicilan'] ?? '';
$kodeKredit = $_POST['kode_kredit'] ?? '';
$tanggal = $_POST['tanggal_cicilan'] ?? '';
$ke = $_POST['cicilanke'] ?? '';
$jumlah = $_POST['jumlah_cicilan'] ?? '';
$sisaKe = $_POST['sisacicilke'] ?? '';
$sisa = $_POST['sisa_cicilan'] ?? '';

if ($kode !== '' && $kodeKredit !== '' && $tanggal !== '' && $ke !== '' && $jumlah !== '' && $sisaKe !== '' && $sisa !== '') {
    $stmt = $conn->prepare("UPDATE bayar_cicilan SET kode_kredit=?, tanggal_cicilan=?, cicilanke=?, jumlah_cicilan=?, sisacicilke=?, sisa_cicilan=? WHERE kode_cicilan=?");
    $stmt->bind_param("ssiiiis", $kodeKredit, $tanggal, $ke, $jumlah, $sisaKe, $sisa, $kode);
    if ($stmt->execute()) {
        $response = ['status' => 1, 'message' => 'Data cicilan berhasil diupdate'];
    } else {
        $response = ['status' => 0, 'message' => 'Update gagal: ' . $stmt->error];
    }
    $stmt->close();
}
echo json_encode($response);
?>
