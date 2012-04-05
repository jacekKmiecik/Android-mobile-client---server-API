<?php
/*

	Script used for updating location associated with a particular vehicle

*/

//load the data needed for database access:
require ("../sql.inc");

mysql_connect($host,$user,$password) or die(mysql_error());
mysql_select_db($db);
mysql_query("SET NAMES 'utf8'"); //needed for proper encoding of Polish letters

//3 arguments: id_vehicle, lat, lng,

//update the coordinates in the table which holds markers for map:
$query = "UPDATE `logistics`.`marker_vehicle` SET `lat` = '".$_REQUEST['lat']."',`updated_on` = CURRENT_TIMESTAMP, `lng` = '".$_REQUEST['lng']."'"; 
$query .= " WHERE `marker_vehicle`.`id_vehicle` ='".$_REQUEST['id_vehicle']."';";

$q=mysql_query($query);
if($q != "") print($q);
else print "0";

//close connection:
mysql_close();
?>