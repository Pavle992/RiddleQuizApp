<?php 

class Place{
	var $indeks;
	var $naziv;
	var $lon;
	var $lat;
	var $riddle;
	var $solution;
	var $hint;
	var $visible;
	var $solved;
	var $id_korisnika;

		public function Place($naziv,$lon,$lat,$riddle,$solution,$hint,$visible,$solved,$id_korisnika){
		
			$this->naziv=$naziv;
			$this->lon=$lon;
			$this->lat=$lat;
			$this->riddle=$riddle;
			$this->solution=$solution;
			$this->hint=$hint;
			$this->visible=$visible;
			$this->solved=$solved;
			$this->id_korisnika=$id_korisnika;
		}
	}

?>