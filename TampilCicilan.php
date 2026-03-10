<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$sql = "
SELECT 
    k.Kode_Kredit AS kode_kredit,
    p.Nama_Customer AS nama_pembeli,
    b.tgl_Cicil AS tanggal_cicilan, 
    b.Cicilan_Ke AS cicilanke,       
    b.Jumlah_Cicilan AS jumlah_cicilan,
    b.Sisa_Cicilan AS sisa_cicilan
FROM bayar_cicilan b
JOIN beli_kredit k ON b.Kode_Kredit = k.Kode_Kredit
JOIN customer p ON k.Email = p.Email
WHERE (b.Kode_Kredit, b.Cicilan_Ke) IN (
    SELECT Kode_Kredit, MAX(Cicilan_Ke)
    FROM bayar_cicilan
    GROUP BY Kode_Kredit
)   
ORDER BY k.Kode_Kredit ASC
";

$result = $conn->query($sql);
$data = [];

if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
} else if (!$result) {
    // Memberi pesan error jika query gagal
    $data = ['error' => 'Query Gagal: ' . $conn->error];
} else {
    // (Opsional) Mengembalikan array kosong jika tidak ada data
    // $data = []; 
}

echo json_encode($data);
