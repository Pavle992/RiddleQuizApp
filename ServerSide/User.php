<?php

	class User{

		var $indeks;
		var $ime;
		var $prezime;
		var $username;
		var $password;
		var $brtel;
		var $score;
		var $bt_device;
		var $imgData;

		public function User($ime,$prezime,$username,$password,$brtel,$score,$bt_device,$imgData){
		
			$this->ime=$ime;
			$this->prezime=$prezime;
			$this->username=$username;
			$this->password=$password;
			$this->brtel=$brtel;
			$this->score=$score;
			$this->bt_device=$bt_device;
			$this->imgData=$imgData;
		}
	}

?>