<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$result = $conn->query("SELECT kode_kredit, ktp, kode_paket, kode_mobil, tanggal_kredit, bayar_kredit, tenor, totalcicil FROM kredit");
$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}
echo json_encode($data);
?>
