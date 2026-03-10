<?php
include 'Koneksi.php';
header('Content-Type: application/json');

$result = $conn->query("SELECT * FROM beli_kredit");

$cash = array();

while ($row = $result->fetch_assoc()) {
	$cash[] = $row;
}

echo json_encode($cash);
