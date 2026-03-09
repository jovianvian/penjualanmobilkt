<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['success' => false, 'message' => ''];
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $kode = $_POST['kode_mobil'] ?? '';
    $merk = $_POST['merk'] ?? '';
    $type = $_POST['type'] ?? '';
    $warna = $_POST['warna'] ?? '';
    $harga = $_POST['harga'] ?? '';
    $fotoBase64 = $_POST['foto_mobil_base64'] ?? '';

    if ($kode !== '' && $merk !== '' && $type !== '' && $warna !== '' && $harga !== '') {
        $fotoPath = null;
        if ($fotoBase64 !== '') {
            $folder = __DIR__ . '/uploads/mobil/';
            if (!file_exists($folder)) {
                mkdir($folder, 0777, true);
            }
            $fileName = $kode . '_' . time() . '.jpg';
            $fullPath = $folder . $fileName;
            if (file_put_contents($fullPath, base64_decode($fotoBase64))) {
                $fotoPath = 'uploads/mobil/' . $fileName;
            }
        }

        $stmt = $conn->prepare("INSERT INTO mobil (kode_mobil, merk, type, warna, harga, foto_mobil) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("ssssis", $kode, $merk, $type, $warna, $harga, $fotoPath);
        $response['success'] = $stmt->execute();
        $response['message'] = $response['success'] ? 'Data mobil berhasil ditambahkan' : ('Gagal menambah data: ' . $stmt->error);
        $stmt->close();
    } else {
        $response['message'] = 'Semua data wajib diisi';
    }
}
echo json_encode($response);
?>

