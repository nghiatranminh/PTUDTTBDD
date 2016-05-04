<?php

	header("Content-Type:application/json");
	include("location.php");
	
	$lat = $_GET['latitude'];
	$lon = $_GET['longtitude'];
	
	if(!empty($lat) && !empty($lon))
	{
		$result = get_location_map($lat, $lon);
		
		$jsondata = json_encode($result);
		
		echo $jsondata;
	}
?>