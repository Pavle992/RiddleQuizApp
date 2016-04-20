<?php 

include "User.php";
include "Place.php";

function dodajKorisnika(User $user){
	$con=new mysqli("localhost","root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			$hash=md5($user->password);
			$query_text = "INSERT INTO user (ime, prezime, username, password, brtel,score,bt_device,imgData) 
				VALUES (". "'$user->ime', '$user->prezime', '$user->username',
				 '$hash', '$user->brtel', '$user->score', '$user->bt_device', '$user->imgData')";


		    $res = $con->query($query_text);
		    if (!$res)
		    {
		        print ("Query failed");
		    }
		    else
		    {
		        return true;
		    }
	    }
	    return false;
}
function vrati_korisnika($imeK,$passK){
		$con=new mysqli("localhost"	,"root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
				// $res je rezultat izvrsenja upita
			$hash=md5($passK);
	        $res = $con->query("select * from user where username='$imeK' AND password='$hash'");
	        if ($res)
	        {
	            // fetch_assoc() pribavlja jedan po jedan red iz rezulata
	            // u redosledu u kom ga je vratio db server i pakuje vrednosti u
	            // niz objekata
	            $useri=array();
	            while ($row = $res->fetch_assoc())
	            {
	            	// $indks=$row['indeks'];

	                $user = new User($row['ime'],
	                        $row['prezime'], $row['username'],$row['password'],$row['brtel'],$row['score'],
	                        $row['bt_device'],base64_encode($row['imgData']));

	                $useri[0]=$user;
	            }
	            // zatvaranje objekta koji cuva rezultat
	            $res->close();
	            // return $kaf;
	            if(empty($useri))
	            	return null;
	            else
	            	return $useri[0];
	        }
	        else
	        {
	            print ("Query failed ");
	        }
	    }
}
function vratiKorisnike(){}
function vratiSveUsernameove(){
	    $con = new mysqli("localhost", "root", "", "riddledb");
	    if ($con->connect_errno)
	    {
	        // u slucaju greske odstampati odgovarajucu poruku
	        print ("Connection error (" . $con->connect_errno . "): $con->connect_error");
	    }
	    else
	    {
	        // $res je rezultat izvrsenja upita
	        $res = $con->query("select * from user");
	        if ($res)
	        {
	            $niz = array();
	            // fetch_assoc() pribavlja jedan po jedan red iz rezulata
	            // u redosledu u kom ga je vratio db server i pakuje vrednosti u
	            // niz objekata
	            $indx=0;
	            while ($row = $res->fetch_assoc())
	            {
	                $username= $row["username"];
	                $niz[$indx] = $username;
	                $indx++;
	            }
	            // zatvaranje objekta koji cuva rezultat
	            $res->close();
	            return $niz;
	        }
	        else
	        {
	            print ("Query failed");
	        }
	    }
}

function dodaj_mesto(Place $mesto){

		$con=new mysqli("localhost","root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			$query_text = "INSERT INTO place (naziv,lon,lat,riddle,solution,hint,visible,solved,id_korisnika) VALUES (".
				"'$mesto->naziv', '$mesto->lon', '$mesto->lat','$mesto->riddle', '$mesto->solution', '$mesto->hint', 0, 0, '$mesto->id_korisnika')";//umesto 0 0 bilo je $mesto->visible i solved
		    $res = $con->query($query_text);
		    if (!$res)
		    {
		        print ("Query failed ".$query_text);
		    }
		    else
		    {
		        return true;
		    }
	    }
	    return false;
	}

function vratiIdKorisnika($username){

		$con=new mysqli("localhost"	,"root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			
	        $res = $con->query("select indeks from user where username='$username'");
	        if ($res)
	        {
	            // fetch_assoc() pribavlja jedan po jedan red iz rezulata
	            // u redosledu u kom ga je vratio db server i pakuje vrednosti u
	            // niz objekata
	            $usernameovi=array();
	            while ($row = $res->fetch_assoc())
	            {
	            	// $indks=$row['indeks'];

	                $usernam = $row['indeks'];

	                $usernameovi[0]=$usernam;
	            }
	            // zatvaranje objekta koji cuva rezultat
	            $res->close();
	            // return $kaf;
	            if(empty($usernameovi))
	            	return null;
	            else
	            	return $usernameovi[0];
	        }
	        else
	        {
	            print ("Query failed ");
	        }
	    }

}

function vratiUsernameKorisnika($idkor){

		$con=new mysqli("localhost"	,"root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			
	        $res = $con->query("select username from user where indeks='$idkor'");
	        if ($res)
	        {
	            // fetch_assoc() pribavlja jedan po jedan red iz rezulata
	            // u redosledu u kom ga je vratio db server i pakuje vrednosti u
	            // niz objekata
	            $usernameovi=array();
	            while ($row = $res->fetch_assoc())
	            {
	            	// $indks=$row['indeks'];

	                $usernam = $row['username'];

	                $usernameovi[0]=$usernam;
	            }
	            // zatvaranje objekta koji cuva rezultat
	            $res->close();
	            // return $kaf;
	            if(empty($usernameovi))
	            	return null;
	            else
	            	return $usernameovi[0];
	        }
	        else
	        {
	            print ("Query failed ");
	        }
	    }

}


function vrati_mesta_korisnika($username){
	    $con = new mysqli("localhost", "root", "", "riddledb");
	    if ($con->connect_errno)
	    {
	        // u slucaju greske odstampati odgovarajucu poruku
	        print ("Connection error (" . $con->connect_errno . "): $con->connect_error");
	    }
	    else
	    {
	    	$id_kor=vratiIdKorisnika($username);
	    	
	        // $res je rezultat izvrsenja upita
	    	$niz_id_prijatelja=vratiIDSvihPrijatelja($id_kor);

	    	$indx=0;
	    	$niz = array();

	    	foreach ($niz_id_prijatelja as $id_prijatelja) {
	    		
		        $res = $con->query("select * from place where id_korisnika='$id_prijatelja'");//$id_prijatelja
		        

		        if ($res)
		        {
		            
		           
		            // $indx=0;
		            while ($row = $res->fetch_assoc())
		            {

		            	$usernameKor=vratiUsernameKorisnika($row["id_korisnika"]);

		                $mesto= new Place($row["naziv"],$row["lon"],$row["lat"],$row["riddle"],
							$row["solution"],$row["hint"],$row["visible"],$row["solved"],$usernameKor);//zbog Saleta!

		                $niz[$indx] = json_encode($mesto);
		                $indx++;
		            }
		            // // zatvaranje objekta koji cuva rezultat
		            $res->close();
		            // return $niz;
		        }
		        else
		        {
		            print ("Query failed");
		        }

		        //$res->close();
	    	}
	    	// zatvaranje objekta koji cuva rezultat
		    
		    return $niz;
	   }
}

function vrati_sopstvena_mesta_korisnika($username){
	    $con = new mysqli("localhost", "root", "", "riddledb");
	    if ($con->connect_errno)
	    {
	        // u slucaju greske odstampati odgovarajucu poruku
	        print ("Connection error (" . $con->connect_errno . "): $con->connect_error");
	    }
	    else
	    {
	    	$id_kor=vratiIdKorisnika($username);
	    	

	    	$indx=0;
	    	$niz = array();
	    		
		        $res = $con->query("select * from place where id_korisnika='$id_kor'");//$id_prijatelja
		        
		        if ($res)
		        {
		            
		           
		            // $indx=0;
		            while ($row = $res->fetch_assoc())
		            {           	

		                $mesto= new Place($row["naziv"],$row["lon"],$row["lat"],$row["riddle"],
							$row["solution"],$row["hint"],$row["visible"],$row["solved"],$username);//zbog Saleta!

		                $niz[$indx] = json_encode($mesto);
		                $indx++;
		            }
		            // // zatvaranje objekta koji cuva rezultat
		            $res->close();
		            return $niz;
		        }
		        else
		        {
		            print ("Query failed");
		        }
	    	}
}

function vratiIDSvihPrijatelja($id_kor){

		$con=new mysqli("localhost"	,"root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			
	        $res = $con->query("SELECT id_b as FR FROM friends WHERE id_a='$id_kor'
								union
								SELECT id_a as FR FROM friends WHERE id_b='$id_kor'");
	        if ($res)
	        {
	            // fetch_assoc() pribavlja jedan po jedan red iz rezulata
	            // u redosledu u kom ga je vratio db server i pakuje vrednosti u
	            // niz objekata
	            $usernameovi=array();
	            $indeks=0;
	            while ($row = $res->fetch_assoc())
	            {
	            	// $indks=$row['indeks'];

	                $usernam = $row['FR'];

	                $usernameovi[$indeks]=$usernam;
	                $indeks++;
	            }
	            // zatvaranje objekta koji cuva rezultat
	            $res->close();
	            // return $kaf;
	            return $usernameovi;
	        }
	        else
	        {
	            print ("Query failed ");
	        }
	    }

}

function dodajBlueToothUredjaj($username,$btdevice)
{
	$con=new mysqli("localhost","root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			echo "<<$username>>";
			$ind_kor=vratiIdKorisnika($username);
			echo "<<$ind_kor>> <<$btdevice>>";
			$query_text = "UPDATE user SET bt_device='$btdevice' WHERE indeks='$ind_kor'";
		    $res = $con->query($query_text);
		    if (!$res)
		    {
		        print ("Query failed");
		    }
		    else
		    {
		        return true;
		    }
	    }
	    return false;
}


function vratiIdKorisnikaNaOsnovuBT($bturedjaj){

		$con=new mysqli("localhost"	,"root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			
	        $res = $con->query("select indeks from user where bt_device='$bturedjaj'");
	        if ($res)
	        {
	            // fetch_assoc() pribavlja jedan po jedan red iz rezulata
	            // u redosledu u kom ga je vratio db server i pakuje vrednosti u
	            // niz objekata
	            $usernameovi=array();
	            while ($row = $res->fetch_assoc())
	            {
	            	// $indks=$row['indeks'];

	                $usernam = $row['indeks'];

	                $usernameovi[0]=$usernam;
	            }
	            // zatvaranje objekta koji cuva rezultat
	            $res->close();
	            // return $kaf;
	            if(empty($usernameovi))
	            	return null;
	            else
	            	return $usernameovi[0];
	        }
	        else
	        {
	            print ("Query failed ");
	        }
	    }

}


function dodaj_prijatelja($username,$blDevice){

		$con=new mysqli("localhost","root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{

			$id_a=vratiIdKorisnika($username);
			$id_b=vratiIdKorisnikaNaOsnovuBT($blDevice);

			$query_text = "INSERT INTO friends (id_a,id_b)
				VALUES (". "'$id_a', '$id_b')";
		    $res = $con->query($query_text);
		    if (!$res)
		    {
		        print ("Query failed");
		    }
		    else
		    {
		        return true;
		    }
	    }
	    return false;
	}

function vrati_username_i_bt_svih_prijatelja($username){
	    $con = new mysqli("localhost", "root", "", "riddledb");
	    if ($con->connect_errno)
	    {
	        // u slucaju greske odstampati odgovarajucu poruku
	        print ("Connection error (" . $con->connect_errno . "): $con->connect_error");
	    }
	    else
	    {
	    	$id_kor=vratiIdKorisnika($username);
	    	
	        // $res je rezultat izvrsenja upita
	    	$niz_id_prijatelja=vratiIDSvihPrijatelja($id_kor);

	    	$indx=0;
	    	$niz = array();

	    	foreach ($niz_id_prijatelja as $id_prijatelja) {
	    		
		        $res = $con->query("select * from user where indeks='$id_prijatelja'");//$id_prijatelja
		        
		        if ($res)
		        {          
		           
		            // $indx=0;
		            while ($row = $res->fetch_assoc())
		            {

		                $obj=(object)array("username"=>$row["username"],"bt_device"=>$row["bt_device"]);

		                $niz[$indx] = json_encode($obj);
		                $indx++;
		            }
		            // // zatvaranje objekta koji cuva rezultat
		            $res->close();
		            // return $niz;
		        }
		        else
		        {
		            print ("Query failed");
		        }

		        //$res->close();
	    	}
	    	// zatvaranje objekta koji cuva rezultat
		    
		    return $niz;
	   }
}	

function obrisi_prijatelja($username,$blDevice){

		$con=new mysqli("localhost","root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{

			$id_a=vratiIdKorisnika($username);
			$id_b=vratiIdKorisnikaNaOsnovuBT($blDevice);

			$query_text = "DELETE FROM friends WHERE id_a='$id_a' AND id_b='$id_b'";
		    $res = $con->query($query_text);
		    if (!$res)
		    {
		        print ("Query failed");
		    }
		    else
		    {
		        return true;
		    }
	    }
	    return false;
	}

function azurirajLokacijuKorisnika($username,$lon,$lat)
{
	$con=new mysqli("localhost","root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			$ind_kor=vratiIdKorisnika($username);
			$query_text = "UPDATE lokacija SET lon=$lon,lat=$lat WHERE id_korisnika=$ind_kor";
		    $res = $con->query($query_text);
		    //$s=mysql_affected_rows();
		    $s=$con->affected_rows;
		    
		    if (!$s)
		    {
		    	
		    	//provera dali se uopste nalazi u bazi lokacija korisnika
		    	$qut="SELECT * FROM lokacija WHERE id_korisnika='$ind_kor'";
		    	$res2 = $con->query($qut);
		    	$rs=$res2->fetch_assoc();
		    	///
		    	
		    	if(!$rs)
		    	{
		        	$query_text1 = "INSERT INTO lokacija (lon,lat,id_korisnika) VALUES ('$lon','$lat','$ind_kor')";
			    	$res1 = $con->query($query_text1);
			    
				    if (!$res1)
				    {
				        print ("Query failed");
				    }
				    else
				    {
				        return true;
				    }
				}
		    }
		    else
		    {
		        return true;
		    }
	    }
	    return true;
}

function vrati_username_lon_i_lat_svih_prijatelja($username){
	    $con = new mysqli("localhost", "root", "", "riddledb");
	    if ($con->connect_errno)
	    {
	        // u slucaju greske odstampati odgovarajucu poruku
	        print ("Connection error (" . $con->connect_errno . "): $con->connect_error");
	    }
	    else
	    {
	    	$id_kor=vratiIdKorisnika($username);
	    	
	        // $res je rezultat izvrsenja upita
	    	$niz_id_prijatelja=vratiIDSvihPrijatelja($id_kor);

	    	$indx=0;
	    	$niz = array();

	    	foreach ($niz_id_prijatelja as $id_prijatelja) {
	    		
		        $res = $con->query("select * from lokacija where id_korisnika='$id_prijatelja'");//$id_prijatelja
		        
		        if ($res)
		        {          
		           
		           $userKor=vratiUsernameKorisnika($id_prijatelja);
		            // $indx=0;
		            while ($row = $res->fetch_assoc())
		            {

		                $obj=(object)array("username"=>$userKor,"lon"=>$row["lon"],"lat"=>$row["lat"]);

		                $niz[$indx] = json_encode($obj);
		                $indx++;
		            }
		            // // zatvaranje objekta koji cuva rezultat
		            $res->close();
		            // return $niz;
		        }
		        else
		        {
		            print ("Query failed");
		        }

		        //$res->close();
	    	}
	    	// zatvaranje objekta koji cuva rezultat
		    
		    return $niz;
	   }
}	

//vracamo sve prijatelje datog korisnika
function vrati_sve_prijatelje_korisnika($username){
	    $con = new mysqli("localhost", "root", "", "riddledb");
	    if ($con->connect_errno)
	    {
	        // u slucaju greske odstampati odgovarajucu poruku
	        print ("Connection error (" . $con->connect_errno . "): $con->connect_error");
	    }
	    else
	    {
	    	$id_kor=vratiIdKorisnika($username);
	    	
	        // $res je rezultat izvrsenja upita
	    	$niz_id_prijatelja=vratiIDSvihPrijatelja($id_kor);

	    	//dodajemo i sebe da bismo se videli na HighScore tabeli
	    	$niz_id_prijatelja[]=$id_kor;

	    	$indx=0;
	    	$niz = array();

	    	foreach ($niz_id_prijatelja as $id_prijatelja) {
	    		
		        $res = $con->query("select * from user where indeks='$id_prijatelja'");//$id_prijatelja
		        
		        if ($res)
		        {         
		            // $indx=0;
		            while ($row = $res->fetch_assoc())
		            {

		                $obj=(object)array("username"=>$row["username"],"ime"=>$row["ime"],"prezime"=>$row["prezime"],
		                					"brtel"=>$row["brtel"],"score"=>$row["score"],"slika"=>base64_encode($row['imgData']));

		                $niz[$indx] = json_encode($obj);
		                $indx++;
		            }
		            // // zatvaranje objekta koji cuva rezultat
		            $res->close();
		            // return $niz;

		        }
		        else
		        {
		            print ("Query failed");
		        }

		        //$res->close();
	    	}
	    	// zatvaranje objekta koji cuva rezultat
		    
		    return $niz;
	   }
}	

function vratiScoreKorisnika($username){

		$con=new mysqli("localhost"	,"root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			
	        $res = $con->query("select score from user where username='$username'");
	        if ($res)
	        {
	            // fetch_assoc() pribavlja jedan po jedan red iz rezulata
	            // u redosledu u kom ga je vratio db server i pakuje vrednosti u
	            // niz objekata
	            $usernameovi=array();
	            while ($row = $res->fetch_assoc())
	            {
	            	// $indks=$row['indeks'];

	                $usernam = $row['score'];

	                $usernameovi[0]=$usernam;
	            }
	            // zatvaranje objekta koji cuva rezultat
	            $res->close();
	            // return $kaf;
	            if(empty($usernameovi))
	            	return null;
	            else
	            	return $usernameovi[0];
	        }
	        else
	        {
	            print ("Query failed ");
	        }
	    }

}


//updejtuj highscore
function updateHighScore($username)
{
	$con=new mysqli("localhost","root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			$score=vratiScoreKorisnika($username);
			$score+=10;
			$query_text = "UPDATE user SET score='$score' WHERE username='$username'";
		    $res = $con->query($query_text);
		    if (!$res)
		    {
		        print ("Query failed");
		    }
		    else
		    {
		        return true;
		    }
	    }
	    return false;
}

//updejtuj highscore
function updateHighScore2($username,$broj)
{
	$con=new mysqli("localhost","root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			$score=vratiScoreKorisnika($username);
			if($broj==1)
				$score+=10;
			else
				$score+=5;
			$query_text = "UPDATE user SET score='$score' WHERE username='$username'";
		    $res = $con->query($query_text);
		    if (!$res)
		    {
		        print ("Query failed");
		    }
		    else
		    {
		        return true;
		    }
	    }
	    return false;
}

function vrati_pitanje_rqt($lat,$log){

	$con=new mysqli("localhost"	,"root","","riddledb");
		if($con->connect_errno){
			echo "Connection error (".$con->connect_errno."): $con->connect_errno";
		}
		else{
			

			$id_kor=vratiIdKorisnika("RQT");
	    	
	       
	    	$indx=0;
	    	$niz = array();
	    	$questions = array();

	    	$lt=floatval($lat);
	    	$lg=floatval($log);

	    	$min=10000;
	    	$imin=0;
	    		
		        $res = $con->query("select * from place where id_korisnika='$id_kor'");//

		        if ($res)
		        {		            
		           
		            // $indx=0;
		            while ($row = $res->fetch_assoc())
		            {

		            	$usernameKor=vratiUsernameKorisnika($row["id_korisnika"]);

		                $mesto= new Place($row["naziv"],$row["lon"],$row["lat"],$row["riddle"],
							$row["solution"],$row["hint"],$row["visible"],$row["solved"],$usernameKor);//zbog Saleta!

		                $dif=abs($lt-$row["lat"])+abs($lg-$row["lon"]);

		                if($dif < $min) {
		                
		                	$min=$dif;	
		            		$imin=$indx;
		                
		                }
		                	
		                $niz[$indx] = $mesto;
		                $indx++;
		            }
		            // // zatvaranje objekta koji cuva rezultat
		            $res->close();
		            // return $niz;

		            $questions[0]=$niz[$imin];

	            // return $kaf;
	            if(empty($questions))
	            	return null;
	            else
	            	return $questions[0];
		        
		        }
		        else
		        {
		            print ("Query failed");
		        }

		        //$res->close();
		    
		    return $niz;

	    }

}

?>