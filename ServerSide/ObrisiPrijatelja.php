<?php 

include "lib.php";

	if(isset($_POST["action"])){

		// $ttt=$_POST["action"];
		$user_bt_json=json_decode($_POST["action"]);
		//sli
		$username=$user_bt_json->username;
		$bt_device=$user_bt_json->bt_device;
		
		$textmessage="";
		if(obrisi_prijatelja($username,$bt_device))
		{
			$textmessage="Prijatelj uspesno obrisan iz baze";	
		}
		else{
			$textmessage="Greska prijatelji nisu obrisani iz baze.";
		}

		echo $textmessage;
		// echo $ttt;
	}
	else
		echo "Nema ga action";
?>