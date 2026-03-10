<?php
include 'Koneksi.php';


header('Content-Type: application/json');
$result=$conn->query("select * from customer");
$customer=array();
while($row=$result->fetch_assoc()){
	$customer[]=$row;
}
echo json_encode($customer);
?>	