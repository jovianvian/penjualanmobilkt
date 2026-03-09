<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$response = ['success' => false, 'message' => ''];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $ktp = $_POST['ktp'] ?? '';
    $nama = $_POST['nama_pembeli'] ?? '';
    $alamat = $_POST['alamat_pembeli'] ?? '';
    $telp = $_POST['telp_pembeli'] ?? '';
    $fotoBase64 = $_POST['foto_ktp_base64'] ?? '';

    if ($ktp && $nama && $alamat && $telp) {
        $fotoPath = null;
        if ($fotoBase64 !== '') {
            $folder = __DIR__ . '/uploads/ktp/';
            if (!file_exists($folder)) {
                mkdir($folder, 0777, true);
            }
            $fileName = $ktp . '_' . time() . '.jpg';
            $fullPath = $folder . $fileName;
            if (file_put_contents($fullPath, base64_decode($fotoBase64))) {
                $fotoPath = 'uploads/ktp/' . $fileName;
            }
        }

        $stmt = $conn->prepare("INSERT INTO pembeli (ktp, nama_pembeli, alamat_pembeli, telp_pembeli, foto_ktp) VALUES (?,?,?,?,?)");
        $stmt->bind_param("sssss", $ktp, $nama, $alamat, $telp, $fotoPath);

        $response['success'] = $stmt->execute();
        $response['message'] = $response['success']
            ? 'Data berhasil disimpan'
            : 'Gagal menyimpan data: ' . $stmt->error;

        $stmt->close();
    } else {
        $response['message'] = 'Semua data harus diisi!';
    }
} else {
    $response['message'] = 'Metode request tidak valid!';
}

echo json_encode($response);
?>
