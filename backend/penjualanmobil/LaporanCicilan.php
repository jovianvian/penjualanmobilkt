<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$periode = $_GET['periode'] ?? 'bulan';
$tanggal = $_GET['tanggal'] ?? date('Y-m');

$sql_base = "SELECT 
                b.Kode_Cicilan AS kode_cicilan,
                c.Nama_Customer AS nama_pembeli,
                br.Merek_Barang AS merk,
                br.Jenis_Barang AS type,
                b.tgl_Cicil AS tgl_cicil,
                b.Cicilan_Ke AS cicilan_ke,
                b.Jumlah_Cicilan AS jumlah_bayar,
                b.Sisa_Cicilan AS sisa_cicilan
            FROM bayar_cicilan b
            JOIN beli_kredit k ON b.Kode_Kredit = k.Kode_Kredit
            JOIN customer c ON k.Email = c.Email
            JOIN barang br ON k.Kode_Barang = br.Kode_Barang";

if ($periode === 'tahun') {
    $sql = "$sql_base WHERE LEFT(b.tgl_Cicil, 4) = '$tanggal' ORDER BY b.tgl_Cicil DESC";
} else {
    $sql = "$sql_base WHERE LEFT(b.tgl_Cicil, 7) = '$tanggal' ORDER BY b.tgl_Cicil DESC";
}

$result = $conn->query($sql);
$data = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
}

echo json_encode($data);
