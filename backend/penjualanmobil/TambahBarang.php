<?php
include 'Koneksi.php';
header('Content-Type: application/json');
$response = ['success' => false, 'message' => ''];
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $kode = $_POST['Kode_Barang'] ?? '';
    $merek = $_POST['Merek_Barang'] ?? '';
    $jenis = $_POST['Jenis_Barang'] ?? '';
    $harga = $_POST['Harga_Barang'] ?? '';
    $foto = $_POST['Foto_Barang'] ?? '';

    if ($kode && $merek && $jenis && $harga && $foto) {
        if (!is_numeric($harga)) {
            $response['message'] = "Harga harus berupa angka!";
        } else {
            $folder = __DIR__ . "/foto_barang/";
            if (!file_exists($folder)) {
                mkdir($folder, 0777, true);
            }

            $nama_file = $kode . "_" . time() . ".jpg";
            $path = $folder . $nama_file;

            if (file_put_contents($path, base64_decode($foto))) {
                $relativePath = "foto_barang/" . $nama_file;

                $stmt = $conn->prepare("INSERT INTO barang (Kode_Barang, Merek_Barang, Jenis_Barang, Harga_Barang, Foto_Barang) VALUES (?, ?, ?, ?, ?)");
                if ($stmt === false) {
                    $response['message'] = "Gagal menyiapkan statement: " . $conn->error;
                } else {
                    $stmt->bind_param("issis", $kode, $merek, $jenis, $harga, $relativePath);
                    if ($stmt->execute()) {
                        $response['success'] = true;
                        $response['message'] = "Data berhasil disimpan";
                    } else {
                        $response['message'] = "Gagal menyimpan data: " . $stmt->error;
                    }
                    $stmt->close();
                }
            } else {
                $response['message'] = "Gagal menyimpan gambar ke folder!";
            }
        }
    } else {
        $response['message'] = "Semua data harus diisi!";
    }
} else {
    $response['message'] = "Metode request tidak valid!";
}
echo json_encode($response);
