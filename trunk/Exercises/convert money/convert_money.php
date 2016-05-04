<?php

	header("Content-Type:application/json");
	include("convert.php");
	
	$from = $_GET['from'];
	$to = $_GET['to'];
	$amount = $_GET['amount'];
	
	if(!empty($from) && !empty($to) && !empty($amount))
	{
		$result = Exchange_Currency($from, $to, $amount);
		deliver_response(200, " Complete", $result);
	}
	
	function deliver_response($status, $status_message, $data)
	{
		header("HTTP/1.1 $status $status_message");
		
		$response['status'] = $status;
		$response['status_message'] = $status_message;
		$response['cost'] = $data;
		
		$jsondata = json_encode($response);
		
		echo $jsondata;
	}
?>