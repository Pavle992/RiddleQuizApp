<?php 
	include "lib.php";

	if(isset($_POST["action"])){
		$user_pass_json=json_decode($_POST["action"]);
		
		$username=$user_pass_json->username;
		$password=$user_pass_json->password;

		// echo "username= ".$username." password= ".$password."     ";
		$korisnik=vrati_korisnika($username,$password);

		echo json_encode($korisnik);
	}
	else {
		echo "Nije postavljen action parametar";
	}
	
?>