<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$result = $conn->query("SELECT kode_paket, uang_muka, tenor, bunga_cicilan FROM paket");
$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}
echo json_encode($data);
?>
