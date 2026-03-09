<?php
include 'Koneksi.php';

// Menerima data dari aplikasi Android
$kode_cash = $_POST['kode_cash'] ?? '';
$email = $_POST['email'] ?? '';
$kode_barang = $_POST['kode_barang'] ?? '';
$tanggal = $_POST['cash_tgl'] ?? ''; // Sesuai dengan key di Java
$bayar = $_POST['cash_bayar'] ?? ''; // Sesuai dengan key di Java

$response = ['status' => 0, 'message' => 'Data tidak lengkap'];

// Pastikan data utama tidak kosong
if ($kode_cash !== '' && $email !== '' && $kode_barang !== '') {

    // Query update ke tabel beli_cash
    // Asumsi nama tabel adalah 'beli_cash'. Sesuaikan jika berbeda.
    $sql = "UPDATE beli_cash 
            SET Email = '$email', 
                Kode_Barang = '$kode_barang', 
                Cash_tgl = '$tanggal', 
                Cash_Bayar = '$bayar' 
            WHERE Kode_Cash = '$kode_cash'";

    if (mysqli_query($conn, $sql)) {
        $response = ["status" => 1, "message" => "Update berhasil"];
    } else {
        $response = ["status" => 0, "message" => "Update gagal: " . mysqli_error($conn)];
    }
}

echo json_encode($response);
