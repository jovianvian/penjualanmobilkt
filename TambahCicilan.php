<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$response = ['success' => false, 'message' => ''];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $kode_cicilan   = $_POST['Kode_Cicilan'] ?? '';
    $kode_kredit    = $_POST['Kode_Kredit'] ?? '';
    $tanggal        = $_POST['tgl_Cicil'] ?? '';
    $cicilanke      = $_POST['Cicilan_Ke'] ?? '';
    $jumlah_cicilan = $_POST['Jumlah_Cicilan'] ?? '';
    $sisa_cicilan   = $_POST['Sisa_Cicilan'] ?? '';

    if ($kode_cicilan && $kode_kredit && $tanggal && $cicilanke && $jumlah_cicilan && $sisa_cicilan) {
        $cek = $conn->prepare("SELECT COUNT(*) AS jml FROM beli_kredit WHERE Kode_Kredit=?");
        if ($cek === false) {
            die(json_encode(['success' => false, 'message' => 'Error SQL Cek Kredit: ' . $conn->error]));
        }

        $cek->bind_param("i", $kode_kredit);
        $cek->execute();
        $result = $cek->get_result()->fetch_assoc();
        $cek->close();

        if ($result['jml'] == 0) {
            echo json_encode(['success' => false, 'message' => 'Kode kredit tidak ditemukan!']);
            exit;
        }

        $stmt = $conn->prepare("INSERT INTO bayar_cicilan 
                (Kode_Cicilan, Kode_Kredit, tgl_Cicil, Cicilan_Ke, Jumlah_Cicilan, Sisa_Cicilan)
                VALUES (?, ?, ?, ?, ?, ?)");

        if ($stmt === false) {
            die(json_encode(['success' => false, 'message' => 'Error SQL Insert: ' . $conn->error]));
        }

        $stmt->bind_param(
            "iisiii",
            $kode_cicilan,
            $kode_kredit,
            $tanggal,
            $cicilanke,
            $jumlah_cicilan,
            $sisa_cicilan
        );

        if ($stmt->execute()) {
            $response['success'] = true;
            $response['message'] = "Data berhasil disimpan";
        } else {
            $response['message'] = "Gagal menyimpan: " . $stmt->error;
        }
        $stmt->close();
    } else {
        $response['message'] = "Semua data wajib diisi!";
    }
} else {
    $response['message'] = "Metode request tidak valid!";
}

$conn->close();
echo json_encode($response);
