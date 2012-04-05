<?php
/*

	Script used for fetching the order info associated with a particular user of the Android client

*/

//load the data needed for database access:
require ("../sql.inc");

mysql_connect($host,$user,$password) or die(mysql_error());
mysql_select_db($db);
mysql_query("SET NAMES 'utf8'"); //needed for proper encoding of Polish letters

$login = $_REQUEST['login'];
$password = $_REQUEST['password'];

//query body:
$q = mysql_query("SELECT id_vehicle FROM driver WHERE login='$login' AND password='$password'");
/*  
   note: the print() method adds a new line ("\n") at the end of printed string
   
   the instructions below send the vehicle ID associated with the client 
   if the authentication was successful, if not: "failed" msg is returned
*/

$e=mysql_fetch_row($q);
if($e[0]) print($e[0]);
else print("failed");
 
//close the connection: 
mysql_close();
?>