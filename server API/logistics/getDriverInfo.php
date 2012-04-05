<?php
/*

	Script used for fetching the information about the driver's vehicle
	(vehicle id, license plates, vehicle model)

*/

//load the data needed for database access:
require ("../sql.inc");

mysql_connect($host,$user,$password) or die(mysql_error());
mysql_select_db($db);
mysql_query("SET NAMES 'utf8'"); //needed for proper encoding of Polish letters

//query body:
$q=mysql_query("SELECT `vehicle`.`id_vehicle`, `vehicle`.`license_plate`, `vehicle`.`brand`
FROM `vehicle` 
LEFT JOIN `driver`
ON `vehicle`.`id_vehicle` = `driver`.`id_vehicle`
WHERE `driver`.`id_vehicle`='".$_REQUEST['id_vehicle']."'");  

 
while($e=mysql_fetch_assoc($q))
        $output[]=$e;

//return JSON encoded data to the client:		
print(json_encode($output));

//close connection:
mysql_close();
?>