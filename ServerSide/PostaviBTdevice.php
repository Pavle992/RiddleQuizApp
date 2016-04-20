<?php 

include "lib.php";

	if(isset($_POST["action"])){

		// $ttt=$_POST["action"];
		$user_bt_json=json_decode($_POST["action"]);
		//sli
		$username=$user_bt_json->username;
		$bt_device=$user_bt_json->bt_device;

		$textmessage="";
		if(dodajBlueToothUredjaj($username,$bt_device))
		{
			$textmessage="Korisnik uspesno dodao bt udedjaj u bazu";	
		}
		else{
			$textmessage="Greska korisnik nije dodao bt udedjaj u bazu.";
		}

		echo $textmessage;
		// echo $ttt;
	}
	else
		echo "Nema ga action";
?>