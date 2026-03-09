<?php
include 'Koneksi.php';

$kode = $_POST['kode_barang'] ?? '';
$merek = $_POST['merek_barang'] ?? '';
$jenis = $_POST['jenis_barang'] ?? '';
$harga = $_POST['harga_barang'] ?? '';

$response = ['status' => 0, 'message' => 'Data tidak lengkap'];

if ($kode !== '' && $merek !== '' && $harga !== '') {
    $sql = "UPDATE barang
            SET Merek_Barang = '$merek',
                Jenis_Barang = '$jenis',
                Harga_Barang = '$harga'
            WHERE Kode_Barang = '$kode'";
    
    if (mysqli_query($conn, $sql)) {
        $response = ["status" => 1, "message" => "Update berhasil"];
    } else {
        $response = ["status" => 0, "message" => "Update gagal: " . mysqli_error($conn)];
    }
}

echo json_encode($response);
?>
