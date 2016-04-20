<?php 
	include "lib.php";

	if(isset($_POST["action"])){

		// $ttt=$_POST["action"];
		$user_json=json_decode($_POST["action"]);
		//slika
		$imageDa=base64_decode($user_json->imgData);
		//$imageDa=mysql_real_escape_string($imageDa);
		$imageDa=addslashes($imageDa);
		
		$user=new User($user_json->ime,$user_json->prezime,$user_json->username,$user_json->password,$user_json->brtel,
			$user_json->score,$user_json->bt_device,$imageDa);
		// $user=new User($user_json->ime,$user_json->prezime,"","","",
		// 	0,"","dawdwa6d56aw5d6wa5dwa6");
		//echo "Sale proveri ovo: ".$user_json->ime." ".$user_json->prezime;
		$textmessage="";
		if(dodajKorisnika($user))
		{
			$textmessage="Korisnik uspesno dodat u bazu";	
		}
		else{
			$textmessage="Greska korisnik nije dodat.";
		}

		echo $textmessage;
		// echo $ttt;
	}
	else
		echo "Nema ga action";

?>