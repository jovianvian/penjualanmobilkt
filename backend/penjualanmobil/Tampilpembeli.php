<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$result = $conn->query("SELECT ktp, nama_pembeli, alamat_pembeli, telp_pembeli, foto_ktp FROM pembeli");
$data = array();

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
}

echo json_encode($data);
?>
