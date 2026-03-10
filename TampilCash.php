<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$result = $conn->query("SELECT kode_cash, ktp, kode_mobil, cash_tgl, cash_bayar FROM beli_cash");
$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}
echo json_encode($data);
?>
