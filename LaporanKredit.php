<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$periode = $_GET['periode'] ?? 'bulan';
$tanggal = $_GET['tanggal'] ?? date('Y-m');

$sql_base = "SELECT 
                k.Kode_Kredit AS kode_kredit,
                c.Nama_Customer AS nama_pembeli,
                b.Merek_Barang AS merk,
                b.Jenis_Barang AS type,
                k.tgl_Kredit AS tgl_kredit,
                k.Total_Cicil AS total_harga,
                k.Tenor AS tenor,
                k.Bayar_Kredit AS cicilan_per_bulan
            FROM beli_kredit k
            JOIN customer c ON k.Email = c.Email
            JOIN barang b ON k.Kode_Barang = b.Kode_Barang";

if ($periode === 'tahun') {
    $sql = "$sql_base WHERE LEFT(k.tgl_Kredit, 4) = '$tanggal' ORDER BY k.tgl_Kredit DESC";
} else {
    $sql = "$sql_base WHERE LEFT(k.tgl_Kredit, 7) = '$tanggal' ORDER BY k.tgl_Kredit DESC";
}

$result = $conn->query($sql);
$data = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
}

echo json_encode($data);
