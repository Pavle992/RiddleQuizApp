<?php 

include "lib.php";

if(isset($_POST["action"])){

		// $ttt=$_POST["action"];
		$mesto_json=json_decode($_POST["action"]);

		$id_kor=vratiIdKorisnika($mesto_json->username);

		$mesto=new Place($mesto_json->name,$mesto_json->longitude,$mesto_json->latitude,$mesto_json->riddle,
			$mesto_json->solution,$mesto_json->hint,FALSE,FALSE,$id_kor);
		
		$textmessage="";
		if(dodaj_mesto($mesto))
		{
			$textmessage="DA";	
		}
		else{
			$textmessage="Greska mesto nije dodato u bazu.";
		}

		echo $textmessage;
		// echo $ttt;
	}
	else
		echo "Nema ga action";
?>