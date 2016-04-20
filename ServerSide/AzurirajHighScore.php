<?php 
	include "lib.php";

	if(isset($_POST["action"])){

		// $ttt=$_POST["action"];
		$user_json=json_decode($_POST["action"]);
		//slika
		$username=$user_json->username;
		$broj=$user_json->broj;
		
		// $user=new User($user_json->ime,$user_json->prezime,"","","",
		// 	0,"","dawdwa6d56aw5d6wa5dwa6");
		//echo "Sale proveri ovo: ".$user_json->ime." ".$user_json->prezime;
		$textmessage="";
		if(updateHighScore2($username,$broj))
		{
			$textmessage="da";	
		}
		else{
			$textmessage="ne";
		}

		echo $textmessage;
		// echo $ttt;
	}
	else
		echo "Nema ga action";

?>