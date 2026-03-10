<?php
include 'Koneksi.php';
header('Content-Type: application/json');

// Gunakan AS untuk mencocokkan dengan Java
$sql = "SELECT 
            Kode_Barang AS kode_barang, 
			Merek_Barang AS merek_barang,
			Jenis_Barang AS jenis_barang,
			Harga_Barang AS harga_barang
        FROM barang";

$result = $conn->query($sql);
$barang = array();
while ($row = $result->fetch_assoc()) {
	$barang[] = $row;
}
echo json_encode($barang);
