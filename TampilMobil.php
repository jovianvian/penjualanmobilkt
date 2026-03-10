<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$result = $conn->query("SELECT kode_mobil, merk, type, warna, harga, foto_mobil FROM mobil");
$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}
echo json_encode($data);
?>
