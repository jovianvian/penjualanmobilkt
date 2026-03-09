<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['status' => 0, 'message' => 'Data tidak lengkap'];
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

    if ($fotoPath !== null) {
        $stmt = $conn->prepare("UPDATE mobil SET merk=?, type=?, warna=?, harga=?, foto_mobil=? WHERE kode_mobil=?");
        $stmt->bind_param("sssiss", $merk, $type, $warna, $harga, $fotoPath, $kode);
    } else {
        $stmt = $conn->prepare("UPDATE mobil SET merk=?, type=?, warna=?, harga=? WHERE kode_mobil=?");
        $stmt->bind_param("sssis", $merk, $type, $warna, $harga, $kode);
    }

    if ($stmt->execute()) {
        $response = ['status' => 1, 'message' => 'Data mobil berhasil diupdate'];
    } else {
        $response = ['status' => 0, 'message' => 'Update gagal: ' . $stmt->error];
    }
    $stmt->close();
}
echo json_encode($response);
?>
