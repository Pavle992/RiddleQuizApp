<?php 

	include "lib.php";

	if(isset($_REQUEST["action"])){

		$usrname_json=json_decode($_REQUEST["action"]);

		$username=$usrname_json->username;
		$lon=$usrname_json->longitude;
		$lat=$usrname_json->latitude;

		if(azurirajLokacijuKorisnika($username,$lon,$lat)){

			$objekti=vrati_username_lon_i_lat_svih_prijatelja($username);
			echo json_encode($objekti);
		}
		else{
			echo "Nije azurirana lokacija korisnika";
		}

	}
	else {
		echo "Nije postavljen action parametar";
	}

?>