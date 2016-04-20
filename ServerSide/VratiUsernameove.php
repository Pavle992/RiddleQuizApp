<?php 
	include "lib.php";

	if(isset($_POST["zahtev"])){
		$usernameovi=vratiSveUsernameove();
		echo json_encode($usernameovi);
	}
	else {
		echo "Nije postavljen zahtev parametar";
	}

?>