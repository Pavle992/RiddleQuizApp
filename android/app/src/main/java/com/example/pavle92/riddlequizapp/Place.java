package com.example.pavle92.riddlequizapp;

/**
 * Created by Vulovic on 6.6.2015.
 */
public class Place
{
    long id;
    String userName;
    String latitude;
    String longitude;
    String name;
    String ridle;
    String solution;
    String hint;
    boolean visible=false;
    boolean solved=false;
    boolean upload=false;

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public Place(){}

    public Place(String lat,String log,String name,String ridle,String solution,String hint)
    {
        this.latitude=lat;
        this.longitude=log;
        this.name=name;
        this.ridle=ridle;
        this.solution=solution;
        this.hint=hint;
        visible=false;
        solved=false;
        upload=false;
        userName="";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Place(String userName,String lat,String log,String name,String ridle,String solution,String hint,boolean visi,boolean sol,boolean up)
    {
        this.userName=userName;

        this.latitude=lat;
        this.longitude=log;
        this.name=name;
        this.ridle=ridle;
        this.solution=solution;
        this.hint=hint;
        visible=visi;
        solved=sol;
        upload=up;
    }
    public Place(int id,String userName,String lat,String log,String name,String ridle,String solution,String hint,boolean visi,boolean sol,boolean up)
    {
        this.userName=userName;
        this.id=id;
        this.latitude=lat;
        this.longitude=log;
        this.name=name;
        this.ridle=ridle;
        this.solution=solution;
        this.hint=hint;
        visible=visi;
        solved=sol;
        upload=up;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public void setRidle(String ridle) {
        this.ridle = ridle;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public boolean isSolved() {
        return solved;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getSolution() {
        return solution;
    }

    public String getHint() {
        return hint;
    }

    public String getRidle() {
        return ridle;
    }

    public String getName() {
        return name;
    }

    public String getLongitude() {
        return longitude;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
