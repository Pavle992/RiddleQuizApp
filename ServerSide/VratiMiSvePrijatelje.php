<?php 

	include "lib.php";

	if(isset($_REQUEST["action"])){

		$usrname_json=json_decode($_REQUEST["action"]);

		$username=$usrname_json->username;
		$objekti=vrati_sve_prijatelje_korisnika($username);
		
		echo json_encode($objekti);
		
	}
	else {
		echo "Nije postavljen action parametar";
	}

?>