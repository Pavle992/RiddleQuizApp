<?php 
	include "lib.php";

	if(isset($_POST["action"])){
		//najblize pitanje koje je postavio RQT

		//echo "Proslo";

		$user_pass_json=json_decode($_POST["action"]);
		
		$lat=$user_pass_json->lat;
		$log=$user_pass_json->log;


		//echo "Koord $lat : $log";
		$question=array();

		$question[0]=vrati_pitanje_rqt($lat,$log);

		echo json_encode($question);
	}
	else {
		echo "Nije postavljen action parametar";
	}
	
?>