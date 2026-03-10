<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$result = $conn->query("SELECT kode_cicilan, kode_kredit, tanggal_cicilan, cicilanke, jumlah_cicilan, sisacicilke, sisa_cicilan FROM bayar_cicilan");
$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}
echo json_encode($data);
?>
