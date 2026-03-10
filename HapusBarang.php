<?php
include "koneksi.php";

$kode_barang = $_POST['kode_barang'];

$response = ["status"=>0, "message"=>"Gagal menghapus"];

if(!empty($kode_barang)){
    $query = "DELETE FROM barang WHERE kode_barang='$kode_barang'";
    if(mysqli_query($conn, $query)) {
        $response["status"] = 1;
        $response["message"] = "Berhasil dihapus";
    }
}

echo json_encode($response);
?>
