package com.example.pavle92.riddlequizapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by Vulovic on 3.6.2015.
 */
public class Player implements Parcelable
{
    private String user;
    private String pass;
    private String latitude;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    private String longitude;
    private String Ime;
    private String Prezime;
    private int broj;
    private Bitmap img;
    private int score=0;
    private String btDevice="";

    public Player(String ime, String prezime,String user,String pass, int broj, Bitmap img)
    {
        Ime = ime;
        Prezime = prezime;
        this.user=user;
        this.pass=pass;
        this.broj = broj;
        this.img=img;
      //  this.img = BitmapFactory.decodeByteArray(img, 0, img.length);
        score=0;
    }

    public void setIme(String ime) {
        Ime = ime;
    }

    public void setPrezime(String prezime) {
        Prezime = prezime;
    }

    public void setBroj(int broj) {
        this.broj = broj;
    }

    public void setImg(byte[] img) {
        this.img = BitmapFactory.decodeByteArray(img, 0, img.length);
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Player(String ime, String prezime,String user, int broj, byte[] img)
    {
        Ime = ime;
        Prezime = prezime;

        this.user=user;
        this.broj = broj;
        this.img = BitmapFactory.decodeByteArray(img, 0, img.length);
        score=0;
    }

    public Player()
    {
        user="";
        Ime="";
        Prezime="";
        pass="";
        broj=0;
        score=0;
        img=null;
    }
    public Player(String u,String p)
    {
        user=u;
        pass=p;

    }
    public  Player(Parcel in)
    {
        readFromParcel(in);
    }
    public Player(String user,int br)
    {
        this.user=user;
        this.score=br;
    }



    public Bitmap getImg()
    {
        return img;
    }

    public Player(String user,int br,byte[] ar)
    {
        this.user=user;
        this.score=br;
        this.img= BitmapFactory.decodeByteArray(ar, 0, ar.length);
    }
    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUser()
    {

        return user;
    }
    public String getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(String btDevice) {
        this.btDevice = btDevice;
    }
    public String getPass() {
        return pass;
    }

    public int getScore() {
        return score;
    }

    public String getIme() {
        return Ime;
    }

    public int getBroj() {
        return broj;
    }

    public String getPrezime() {

        return Prezime;
    }

    public  String getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,90, outputStream);
        byte[] bytes= outputStream.toByteArray();
        String img_str= Base64.encodeToString(bytes, Base64.DEFAULT);
        Log.e("prolaz","proslo");
        return img_str;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(this.Ime);
        parcel.writeString(this.Prezime);
        parcel.writeInt(this.broj);
        // parcel.writeByteArray(this.getBitmapAsByteArray(this.img));
    }
    private void readFromParcel(Parcel in)
    {
        this.Ime=in.readString();
        this.Prezime=in.readString();
        this.broj=in.readInt();
    }


    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Player createFromParcel(Parcel in) {
                    return new Player(in);
                }

                public Player[] newArray(int size) {
                    return new Player[size];
                }
            };
}
