<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$sql = "SELECT 
            b.Kode_Cash AS kode_cash, 
            c.Nama_Customer AS nama_pembeli, 
            br.Merek_Barang AS merk, 
            br.Jenis_Barang AS type, 
            b.Cash_tgl AS cash_tgl, 
            b.Cash_Bayar AS cash_bayar 
        FROM beli_cash b 
        JOIN customer c ON b.Email = c.Email 
        JOIN barang br ON b.Kode_Barang = br.Kode_Barang
        ORDER BY b.Kode_Cash DESC";

$result = $conn->query($sql);
$data = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
} else {
    $data['error'] = $conn->error;
}

echo json_encode($data);
