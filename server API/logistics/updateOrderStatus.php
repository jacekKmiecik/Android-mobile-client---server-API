<?php
/*

	Script used for updating an order status in the database

*/

//load the data needed for database access:
require ("../sql.inc");

mysql_connect($host,$user,$password) or die(mysql_error());
mysql_select_db($db);
mysql_query("SET NAMES 'utf8'"); //needed for proper encoding of Polish letters

$status = $_REQUEST['status'];
$id_order = $_REQUEST['id_order'];

if($status == "IN_PROGRESS"){
	$q=mysql_query("UPDATE `logistics`.`order` SET `status` = '$status' WHERE `order`.`id_order` ='$id_order';");
	if($q != "") print($q);
	else print "0";
}
else{ //change the order status to completed or rejected
	if($status == "REJECTED"){ //the id_vehicle is set to null
		$q=mysql_query("UPDATE `logistics`.`order` SET `status` = '$status', `id_vehicle` = null WHERE `order`.`id_order` ='$id_order';");
		if($q != "") print($q);
		else print "0";
	}
	else{
		$q=mysql_query("UPDATE `logistics`.`order` SET `status` = '$status' WHERE `order`.`id_order` ='$id_order';");
		if($q != "") print($q);
		else print "0";
	}
	$q2=mysql_query("UPDATE `logistics`.`vehicle` SET `id_current_order` = null WHERE `vehicle`.`id_current_order` ='$id_order';");
	if($q2 != "") print($q2);
	else print "0";
} 

//close connection:
mysql_close();
?>