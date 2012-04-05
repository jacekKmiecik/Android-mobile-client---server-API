<?php
/*

	Script used for fetching data about the order associated with a particular user

*/

//load the data needed for database access:
require ("../sql.inc");

mysql_connect($host,$user,$password) or die(mysql_error());
mysql_select_db($db);
mysql_query("SET NAMES 'utf8'"); //needed for proper encoding of Polish letters
$output = null;

//query body:
$q=mysql_query("SELECT `vehicle`.`id_vehicle`, `order`.`id_order`, `adr_start`.`address` AS address_start, `adr_end`.`address` AS address_end, `order`.`status`
FROM `vehicle`  
LEFT JOIN `order`
ON `vehicle`.`id_current_order` = `order`.`id_order`
JOIN `address` AS adr_start
ON `adr_start`.`id_address` = `order`.`id_address_start`
JOIN `address` AS adr_end
ON `adr_end`.`id_address` = `order`.`id_address_end`
WHERE `vehicle`.`id_vehicle`='".$_REQUEST['id_vehicle']."'");  
 
while($e=mysql_fetch_assoc($q))
        $output[]=$e;

//return JSON encoded data:
if($output != null) print(json_encode($output));
else print("0");

//close connection:
mysql_close();
?>