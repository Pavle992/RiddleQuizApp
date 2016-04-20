<?php 

	include "lib.php";

	if(isset($_POST["action"])){

		$usrname_json=json_decode($_POST["action"]);

		$mesta=vrati_mesta_korisnika($usrname_json->username);
		echo json_encode($mesta);
	}
	else {
		echo "Nije postavljen action parametar";
	}

?>