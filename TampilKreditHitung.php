<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$sql = "
SELECT 
    k.Kode_Kredit AS kode_kredit,
    k.Email AS Email, 
    k.Bayar_Kredit AS bayar_kredit,
    k.Tenor AS tenor,
    k.Total_Cicil AS totalcicil,
    COALESCE(COUNT(b.Kode_Cicilan), 0) AS jumlah_cicilan
FROM beli_kredit k
LEFT JOIN bayar_cicilan b ON k.Kode_Kredit = b.Kode_Kredit
GROUP BY k.Kode_Kredit, k.Email, k.Bayar_Kredit, k.Tenor, k.Total_Cicil
";

$result = $conn->query($sql);
$data = [];

if ($result === false) {
    echo json_encode(['error' => 'Query Gagal: ' . $conn->error]);
    exit;
}

while ($row = $result->fetch_assoc()) {
    $jumlah_cicilan = (int)$row['jumlah_cicilan'];

    $row['cicilan_ke'] = $jumlah_cicilan + 1;
    $row['sisa_ke'] = (int)$row['tenor'] - $jumlah_cicilan;

    $sisa_harga = (int)$row['totalcicil'] - ((int)$row['bayar_kredit'] * $jumlah_cicilan);
    $row['sisa_harga'] = $sisa_harga;

    $data[] = $row;
}

echo json_encode($data);
