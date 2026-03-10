<?php
include 'Koneksi.php';
header('Content-Type: application/json');

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
error_reporting(E_ALL);
ini_set('display_errors', 1);

$response = ['success' => false, 'message' => ''];

try {
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $kode   = $_POST['Kode_Cash'] ?? '';
        $email  = $_POST['Email'] ?? '';
        $barang = $_POST['Kode_Barang'] ?? '';
        $tgl    = $_POST['Cash_tgl'] ?? '';

        if ($kode && $email && $barang && $tgl) {
            // Ambil harga barang
            $stmt = $conn->prepare("SELECT Harga_Barang FROM barang WHERE Kode_Barang = ?");
            $stmt->bind_param("s", $barang);
            $stmt->execute();
            $stmt->bind_result($harga);
            $stmt->fetch();
            $stmt->close();

            if ($harga) {
                // Insert ke tabel beli_cash
                $stmt2 = $conn->prepare("
                    INSERT INTO beli_cash (Kode_Cash, Email, Kode_Barang, Cash_tgl, Cash_Bayar)
                    VALUES (?, ?, ?, ?, ?)
                ");
                $stmt2->bind_param("ssisi", $kode, $email, $barang, $tgl, $harga);
                $stmt2->execute();
                $stmt2->close();

                $response['success'] = true;
                $response['message'] = "Data berhasil ditambahkan";
            } else {
                $response['message'] = "Harga barang tidak ditemukan untuk kode $barang";
            }
        } else {
            $response['message'] = "Semua data wajib diisi!";
        }
    } else {
        $response['message'] = "Metode request tidak valid!";
    }
} catch (mysqli_sql_exception $e) {
    $response['message'] = "MySQL Error: " . $e->getMessage();
}

echo json_encode($response);
