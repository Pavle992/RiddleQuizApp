<?php 

	include "lib.php";

	if(isset($_POST["action"])){

		$usrname_json=json_decode($_POST["action"]);

		$objekti=vrati_username_i_bt_svih_prijatelja($usrname_json->username);
		echo json_encode($objekti);
	}
	else {
		echo "Nije postavljen action parametar";
	}

?>