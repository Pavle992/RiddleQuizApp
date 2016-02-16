package com.example.pavle92.riddlequizapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MyPlacesHTTPHelper {
    private static String IP_ADDRESS = "192.168.1.7";

    public static String SendMyPlace(Place place, String userName) {

        String retStr = "";

        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/DodajMesto.php");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", "por1");


            JSONObject data = new JSONObject();

            data.put("name", place.getName());
            data.put("riddle", place.getRidle());

            Log.e("http", place.getName());

            data.put("solution", place.getSolution());
            data.put("hint", place.getHint());

            Log.e("http", "por2");

            data.put("latitude", place.getLatitude());
            data.put("longitude", place.getLongitude());

            data.put("username", userName);

            Log.e("http", "por3");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            Log.e("http", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                retStr = inputStreamToString(conn.getInputStream());
            } else
                retStr = String.valueOf("Error: " + responseCode);

            Log.e("http", retStr);

        } catch (Exception e) {
            Log.e("http", "greska");
        }
        return retStr;
    }

    public static String UpdateScore(String userName,int br) {

        String retStr = "";

        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/AzurirajHighScore.php");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", "por1");


            JSONObject data = new JSONObject();

            data.put("username", userName);
            data.put("broj", br);

            Log.e("http", "por3");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            Log.e("http", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                retStr = inputStreamToString(conn.getInputStream());
            } else
                retStr = String.valueOf("Error: " + responseCode);

            Log.e("http", retStr);

        } catch (Exception e) {
            Log.e("http", "greska");
        }
        return retStr;
    }

    public static ArrayList<Place> getPlaces(String userName) {
        ArrayList<Place> places = new ArrayList<Place>();
        String retStr = null;
        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/VratiMestaKorisnika.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", "por1");

            JSONObject data = new JSONObject();
            data.put("username", userName);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                Log.e("imena", str);
                JSONArray jsonArray = new JSONArray(str);

                Log.e("Petlja", "Pet");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));

                    Log.e("Obj", "Obj");

                    Place place = new Place();

                    place.setUserName(jsonObject.getString("id_korisnika"));

                    Log.e("USer", jsonObject.getString("id_korisnika"));

                    place.setName(jsonObject.getString("naziv"));
                    place.setLongitude(jsonObject.getString("lon"));
                    place.setLatitude(jsonObject.getString("lat"));
                    place.setRidle(jsonObject.getString("riddle"));
                    place.setSolution(jsonObject.getString("solution"));
                    place.setHint(jsonObject.getString("hint"));

                    Log.e("Hint", jsonObject.getString("hint"));

                    place.setVisible(Boolean.valueOf(jsonObject.getString("visible")));
                    place.setSolved(Boolean.valueOf(jsonObject.getString("solved")));


                    places.add(place);
                    //   Log.e("Mesto JSON: ",jsonArray.getString(i));
//                    Log.e("Mesto JSON: ", jsonArray.getJSONObject(i).toString());

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            Log.e("Greska", "greska");

        }
        return places;
    }

    public static ArrayList<Place> getMyPlaces(String userName) {
        ArrayList<Place> places = new ArrayList<Place>();
        String retStr = null;
        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/VratiSopstvenaMesta.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", "por1");

            JSONObject data = new JSONObject();
            data.put("username", userName);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                Log.e("imena", str);
                JSONArray jsonArray = new JSONArray(str);

                Log.e("Petlja", "Pet");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));

                    Log.e("Obj", "Obj");

                    Place place = new Place();

                    place.setUserName(jsonObject.getString("id_korisnika"));

                    Log.e("USer", jsonObject.getString("id_korisnika"));

                    place.setName(jsonObject.getString("naziv"));
                    place.setLongitude(jsonObject.getString("lon"));
                    place.setLatitude(jsonObject.getString("lat"));
                    place.setRidle(jsonObject.getString("riddle"));
                    place.setSolution(jsonObject.getString("solution"));
                    place.setHint(jsonObject.getString("hint"));

                    Log.e("Hint", jsonObject.getString("hint"));

                    place.setVisible(Boolean.valueOf(jsonObject.getString("visible")));
                    place.setSolved(Boolean.valueOf(jsonObject.getString("solved")));


                    places.add(place);
                    //   Log.e("Mesto JSON: ",jsonArray.getString(i));
//                    Log.e("Mesto JSON: ", jsonArray.getJSONObject(i).toString());

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            Log.e("Greska", "greska");

        }
        return places;
    }

    public static ArrayList<Player> getFriends(String userName) {
        ArrayList<Player> players = new ArrayList<Player>();
        String retStr = null;
        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/VratiUserBTSvihPrijatelja.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", "por1");

            JSONObject data = new JSONObject();
            data.put("username", userName);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                Log.e("http", str);
                JSONArray jsonArray = new JSONArray(str);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));

                    Log.e("Obj", "Obj");

                    Player player = new Player();

                    player.setUser(jsonObject.getString("username"));
                    player.setBtDevice(jsonObject.getString("bt_device"));

                    Log.e("Ime And BT", player.getUser() + " " + player.getBtDevice());

                    players.add(player);

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            Log.e("Greska", "greska");

        }
        return players;
    }

    public static ArrayList<Player> getFriendsLocations(String userName, String lat, String lng) {
        ArrayList<Player> players = new ArrayList<Player>();
        String retStr = null;
        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/AzurirajVratiPolizajSvihPrijatelja.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", userName + " " + lat + " " + lng);

            JSONObject data = new JSONObject();
            data.put("username", userName);
            data.put("latitude", lat);
            data.put("longitude", lng);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();
            Log.e("Code", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                Log.e("HHHHH", str);
                JSONArray jsonArray = new JSONArray(str);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));

                    Log.e("Obj", "Obj");

                    Player player = new Player();

                    player.setUser(jsonObject.getString("username"));
                    player.setLatitude(jsonObject.getString("lat"));
                    player.setLongitude(jsonObject.getString("lon"));

                    Log.e("Ime And Lat/Lng", player.getUser() + " " + player.getLatitude() + " " + player.getLongitude());

                    players.add(player);

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            Log.e("Greska", "greska");

        }
        return players;
    }

    public static ArrayList<Player> getFriendsData(String userName) {
        ArrayList<Player> players = new ArrayList<Player>();
        String retStr = null;
        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/VratiMiSvePrijatelje.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", userName);

            JSONObject data = new JSONObject();
            data.put("username", userName);


            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();
            Log.e("Code", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                Log.e("HHHHH", str);
                JSONArray jsonArray = new JSONArray(str);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));

                    Log.e("Obj", "Obj");

                    Player player = new Player();

                    player.setUser(jsonObject.getString("username"));
                    player.setIme(jsonObject.getString("ime"));
                    player.setPrezime(jsonObject.getString("prezime"));
                    player.setScore(Integer.valueOf(jsonObject.getString("score")));
                    player.setBroj(Integer.valueOf(jsonObject.getString("brtel")));
                    player.setImg(Base64.decode(jsonObject.getString("slika"), Base64.DEFAULT));
                    Log.e("Ime And Lat/Lng", player.getUser() + " " + player.getLatitude() + " " + player.getLongitude());

                    players.add(player);

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            Log.e("Greska", "greska");

        }
        return players;
    }

    //Ne bi trebalo vise da se koristi
    public static String UdatePlayerBT(String userName, String device) {

        String retStr = "";

        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/PostaviBTdevice.php");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Log.e("http", "por1");

            JSONObject data = new JSONObject();

            data.put("username", userName);
            data.put("bt_device", device);

            Log.e("http", "por3");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            Log.e("http", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                retStr = inputStreamToString(conn.getInputStream());
            } else
                retStr = String.valueOf("Error: " + responseCode);

            Log.e("http", retStr);

        } catch (Exception e) {
            Log.e("http", "greska");
        }
        return retStr;
    }

    public static String FriendsRegistration(String userName, String device) {

        String retStr = "";

        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/DodajPrijatelja.php");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Log.e("http", "por1");

            JSONObject data = new JSONObject();

            data.put("username", userName);
            data.put("bt_device", device);

            Log.e("http", "por3");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            Log.e("http", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                retStr = inputStreamToString(conn.getInputStream());
            } else
                retStr = String.valueOf("Error: " + responseCode);

            Log.e("http", retStr);

        } catch (Exception e) {
            Log.e("http", "greska");
        }
        return retStr;
    }

    public static String FriendUnRegister(String userName, String device) {

        String retStr = "";

        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/ObrisiPrijatelja.php");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Log.e("http", "por1");

            JSONObject data = new JSONObject();

            data.put("username", userName);
            data.put("bt_device", device);

            Log.e("http", "por3");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            Log.e("http", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                retStr = inputStreamToString(conn.getInputStream());
            } else
                retStr = String.valueOf("Error: " + responseCode);

            Log.e("http", retStr);

        } catch (Exception e) {
            Log.e("http", "greska");
        }
        return retStr;
    }

    public static String SendMyPlayer(Player player) {

        String retStr = "";

        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/DodajKorisnika.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", "por1");


            JSONObject data = new JSONObject();

            data.put("ime", player.getIme());
            data.put("prezime", player.getPrezime());
            Log.e("http", player.getIme() + " " + player.getPrezime());
            data.put("username", player.getUser());
            data.put("password", player.getPass());
            Log.e("http", "por2");
            data.put("brtel", player.getBroj());
            data.put("score", player.getScore());
            data.put("bt_device", player.getBtDevice());
            String sl = player.getBitmapAsByteArray(player.getImg());
            //tring sl = getStringImage((player.getImg()));
            data.put("imgData", sl);
            //Log.e("Slika", String.valueOf(sl.length()));
            Log.e("http", "por3");

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();
            Log.e("http", "por4");
            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();
            retStr = String.valueOf("Error: " + responseCode);
            Log.e("http", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                retStr = inputStreamToString(conn.getInputStream());
            } else
                retStr = String.valueOf("Error: " + responseCode);

            Log.e("http", retStr);

        } catch (Exception e) {
            Log.e("http", "greska1");
            retStr = "Error during upload";
        }
        return retStr;
    }


    public static Player SendUserAndPass(String user, String pass) {

        String retStr = "";
        Player player = new Player();
        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/VratiUsera.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", "por1");


            JSONObject data = new JSONObject();


            data.put("username", user);
            data.put("password", pass);
            Log.e("http", "por2");


            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", data.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();
            Log.e("http", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                retStr = inputStreamToString(conn.getInputStream());
                Log.e("UserA", retStr);
                JSONObject object = new JSONObject(retStr);
                player.setIme(object.getString("ime"));
                Log.e("Ime", object.getString("ime"));

                player.setPrezime(object.getString("prezime"));
                Log.e("Preziem", object.getString("prezime"));

                player.setUser(object.getString("username"));
                player.setBroj(Integer.parseInt((String) object.get("brtel")));
                player.setBtDevice(object.getString("bt_device"));
                Log.e("BT", object.getString("bt_device"));

                player.setImg(Base64.decode(object.getString("imgData"), Base64.DEFAULT));
                Log.e("Slika", "AAA");

            } else
                Log.e("Error: ", String.valueOf(responseCode));


        } catch (Exception e) {
            Log.e("Greska", "greska");
            retStr = "Error during upload";
        }
        Log.e("Kraj", "Kraj");
        return player;
    }


    public static List<String> getPlayers() {
        List<String> names = new ArrayList<String>();
        String retStr = null;
        try {
            URL url = new URL("http://" + IP_ADDRESS + "/RiddleQuizApp/ServerSide/VratiUserNameove.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", "por1");


            Uri.Builder builder = new Uri.Builder().appendQueryParameter("zahtev", "da");
            String query = builder.build().getEncodedQuery();

            Log.e("http", "por4");

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            Log.e("http", "por5");
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                Log.e("imena", str);
                JSONArray jsonArray = new JSONArray(str);

                for (int i = 0; i < jsonArray.length(); i++) {
                    String name = jsonArray.getString(i);
                    Log.e("Ime", name);
                    names.add(name);
                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            Log.e("Greska", "greska");

        }
        return names;
    }


    private static String inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = bf.readLine()) != null) {
                total.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return total.toString();
    }

    private static String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}
