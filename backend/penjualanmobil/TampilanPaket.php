<?php
include 'Koneksi.php';
header('Content-Type: application/json');

// Gunakan AS untuk mencocokkan dengan Java
$sql = "SELECT 
            Kode_Paket AS kode_paket, 
            Uang_Muka AS uang_muka, 
            Tenor AS tenor, 
            Bunga_Cicilan AS bunga_cicilan 
        FROM paket";

$result = $conn->query($sql);
$paket = array();
while ($row = $result->fetch_assoc()) {
	$paket[] = $row;
}
echo json_encode($paket);
