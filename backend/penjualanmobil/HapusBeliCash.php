<?php
include 'Koneksi.php';

// Menerima data kode_cash dari aplikasi Android
$kode_cash = $_POST['kode_cash'] ?? '';

$response = ['status' => 0, 'message' => 'Gagal menghapus data'];

if (!empty($kode_cash)) {
    // Query hapus data berdasarkan Kode_Cash
    // Pastikan nama tabel sesuai dengan database Anda (misal: beli_cash)
    $sql = "DELETE FROM beli_cash WHERE Kode_Cash = '$kode_cash'";

    if (mysqli_query($conn, $sql)) {
        $response = ['status' => 1, 'message' => 'Data berhasil dihapus'];
    } else {
        $response = ['status' => 0, 'message' => 'Error: ' . mysqli_error($conn)];
    }
} else {
    $response = ['status' => 0, 'message' => 'Kode Cash tidak ditemukan'];
}

echo json_encode($response);
