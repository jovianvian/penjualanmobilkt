<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$response = ['success' => false, 'message' => ''];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $kode   = $_POST['Kode_Paket'] ?? '';
    $nama   = $_POST['Nama_Paket'] ?? '';
    $barang = $_POST['Kode_Barang'] ?? '';
    $harga  = $_POST['Harga_Paket'] ?? '';

    if ($kode !== '' && $nama !== '' && $barang !== '' && $harga !== '') {
        if (!is_numeric($harga)) {
            $response['message'] = "Harga harus berupa angka!";
        } else {
            $stmt = $conn->prepare("INSERT INTO paket (Kode_Paket, Nama_Paket, Kode_Barang, Harga_Paket) VALUES (?,?,?,?)");
            $stmt->bind_param("sssi", $kode, $nama, $barang, $harga);

            $response['success'] = $stmt->execute();
            $response['message'] = $response['success']
                ? "Data berhasil disimpan"
                : "Gagal menyimpan data: " . $stmt->error;

            $stmt->close();
        }
    } else {
        $response['message'] = "Semua data harus diisi!";
    }
} else {
    $response['message'] = "Metode request tidak valid!";
}

echo json_encode($response);
?>
