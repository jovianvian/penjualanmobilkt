<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$response = ['success' => false, 'message' => ''];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    $kode_kredit    = $_POST['kode_kredit'] ?? '';
    $email          = $_POST['email'] ?? '';
    $kode_paket     = $_POST['kode_paket'] ?? '';
    $kode_barang    = $_POST['kode_barang'] ?? '';
    $tanggal        = $_POST['tanggal_kredit'] ?? '';
    $bayar_kredit   = $_POST['bayar_kredit'] ?? '';
    $tenor          = $_POST['tenor'] ?? '';
    $totalcicil     = $_POST['total_cicil'] ?? '';

    if ($kode_kredit && $email && $kode_paket && $kode_barang && $tanggal && $bayar_kredit && $tenor && $totalcicil) {

        $stmt = $conn->prepare(
            "INSERT INTO beli_kredit (Kode_Kredit, Email, Kode_Paket, Kode_Barang, tgl_Kredit, Bayar_Kredit, Tenor, Total_Cicil)
             VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        );

        $stmt->bind_param(
            "isiisiii",
            $kode_kredit,
            $email,
            $kode_paket,
            $kode_barang,
            $tanggal,
            $bayar_kredit,
            $tenor,
            $totalcicil
        );

        if ($stmt->execute()) {
            $response['success'] = true;
            $response['message'] = "Data berhasil disimpan";
        } else {
            $response['message'] = "Gagal menyimpan data: " . $stmt->error;
        }
        $stmt->close();
    } else {
        $response['message'] = "Semua data wajib diisi! (PHP Gagal Validasi)";
    }
} else {
    $response['message'] = "Metode Request Tidak Valid";
}

$conn->close();
echo json_encode($response);
