package com.example.pavle92.riddlequizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vidanovic on 10.01.2016
 */
public class DBAdapterPlaces
{
    static String DATABASE_NAME="";
    static String DATABASE_TABLE="TabelaMesta";
    static final int DATABASE_VERSION=2;

    static final String ROW_ID="_id";
    static final String ROW_Lat="latitude";
    static final String ROW_Log="longitude";
    static final String ROW_Name ="name";
    static final String ROW_Solution ="solution";
    static final String ROW_Ridle ="ridle";
    static final String Row_Visible="visible";
    static final String Row_Solved="solved";
    static final String Row_Upload="upload";
    static final String ROW_Hint="hint";
    static final String ROW_User="user";

    private static final String CREATE_DATABASE="create table "+DATABASE_TABLE+" (" +
            ROW_ID+" integer primary key autoincrement," +
            ROW_User+" text not null," +
            ROW_Lat+" text not null," +
            ROW_Log+" text not null," +
            ROW_Name +" text not null,"+
            ROW_Ridle +" text not null," +
            ROW_Solution +" text not null," +
            Row_Upload +" text not null," +
            ROW_Hint+" text not null," +
            Row_Visible+" text not null," +
            Row_Solved+" text not null);";

    SQLiteDatabase sqlDB;
    DBHelper helperDB;
    Context con;

    public DBAdapterPlaces(Context c,String user)
    {
        this.DATABASE_NAME=user;
        this.con=c;
        helperDB=new DBHelper(con);

    }

    public DBAdapterPlaces OpenDB()
    {
        sqlDB=helperDB.getWritableDatabase();
        return  this;
    }

    public void CloseDB()
    {
        helperDB.close();
    }

    public void SavePlaces(List<Place> places)
    {
        for (Place place:places)
            SavePlace(place);
    }
    public void SavePlace(Place place)
    {
        ContentValues cv=new ContentValues();

        cv.put(ROW_User,place.getUserName());
        cv.put(ROW_Lat,place.getLatitude());
        cv.put(ROW_Log,place.getLongitude());
        cv.put(ROW_Ridle,place.getRidle());
        cv.put(ROW_Name,place.getName());
        cv.put(Row_Visible,place.isVisible());
        cv.put(Row_Solved,String.valueOf(false));
        cv.put(Row_Upload,String.valueOf(false));
        cv.put(ROW_Hint,place.getHint());
        cv.put(ROW_Solution,place.getSolution());

        //int brPogba=sqlDB.update(DATABASE_TABLE,cv,ROW_Lat+"='"+place.getLatitude()+"' AND " + ROW_Log+"= '"+place.getLongitude()+"'  AND " + Row_Solved+"= '"+false+"' AND " + Row_Visible+"= '"+false+"'",null);

        //if (brPogba == 0)
            sqlDB.insert(DATABASE_TABLE, null, cv);
    }

     public void UpdatePlaceVisible(String lat,String log)
    {
        ContentValues cv=new ContentValues();
        cv.put(Row_Visible,String.valueOf(true));
        sqlDB.update(DATABASE_TABLE, cv, ROW_Lat + "='" + lat + "' AND " + ROW_Log + "= '" + log + "' ", null);
    }
    public void UpdatePlaceSolved(String lat,String log)
    {
        ContentValues cv=new ContentValues();
        cv.put(Row_Solved, String.valueOf(true));
        int id=sqlDB.update(DATABASE_TABLE,cv,ROW_Lat+"='"+lat+"' AND " + ROW_Log+"= '"+log+"' ",null);
    }
    public void UpdatePlaceUploaded(String lat,String log)
    {
        //Prepravi
        ContentValues cv=new ContentValues();
        cv.put(Row_Upload,String.valueOf(true));
        sqlDB.update(DATABASE_TABLE, cv, ROW_Lat + "='" + lat + "' AND " + ROW_Log + "= '" + log + "' ", null);
    }
    public void UpdatePlace(String lat,String log,String title,String ridle,String solution,String hint)
    {
        ContentValues cv=new ContentValues();

        cv.put(ROW_Ridle,ridle);
        cv.put(ROW_Name,title);
        cv.put(ROW_Solution,solution);
        cv.put(ROW_Hint,hint);
        sqlDB.update(DATABASE_TABLE, cv, ROW_Lat + "='" + lat + "' AND " + ROW_Log + "= '" + log + "' ", null);
    }
    public void DeletePlace(String lat,String log)
    {
        sqlDB.delete(DATABASE_TABLE, ROW_Lat + "='" + lat + "' AND " + ROW_Log + "= '" + log + "' ", null);
    }
    public void ClearAll()
    {
        sqlDB.delete(DATABASE_TABLE,null,null);
    }


    public ArrayList<Place> getPlaceses()
{
    ArrayList<Place> places=new ArrayList<Place>();

    String rez="";
    Cursor c=sqlDB.query(true, DATABASE_TABLE, new String[]{ROW_User, ROW_Lat, ROW_Log, ROW_Ridle, ROW_Solution, Row_Upload, ROW_Hint, Row_Visible, Row_Solved, ROW_Name}, null, null, null, null, null, null);

    int user=c.getColumnIndex(ROW_User);
    int lat=c.getColumnIndex(ROW_Lat);
    int log=c.getColumnIndex(ROW_Log);
    int ridle=c.getColumnIndex(ROW_Ridle);
    int solution=c.getColumnIndex(ROW_Solution);
    int visi=c.getColumnIndex(Row_Visible);
    int tit=c.getColumnIndex(ROW_Name);
    int sol=c.getColumnIndex(Row_Solved);
    int up=c.getColumnIndex(Row_Upload);
    int hint=c.getColumnIndex(ROW_Hint);

    if(c!=null)
    {

        for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {

            places.add(new Place(c.getString(user),c.getString(lat),c.getString(log),c.getString(tit),c.getString(ridle),c.getString(solution),c.getString(hint),Boolean.parseBoolean(c.getString(visi)),Boolean.parseBoolean(c.getString(sol)),Boolean.parseBoolean(c.getString(up))));
        }

    }

    return places;
}
    public ArrayList<String> getUserNames()
    {
        ArrayList<String> userNames=new ArrayList<String>();

        String rez="";
        Cursor c=sqlDB.query(true, DATABASE_TABLE, new String[]{ROW_User}, null, null, null, null, null, null);

        int user=c.getColumnIndex(ROW_User);


        if(c!=null)
        {

            for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
            {

                userNames.add(c.getString(user));
            }

        }

        return userNames;
    }
    public ArrayList<Place> getMyPlaceses(String userName)
    {
        ArrayList<Place> places=new ArrayList<Place>();

        String rez="";
        Cursor c=sqlDB.query(true, DATABASE_TABLE, new String[]{ROW_User, ROW_Lat, ROW_Log, ROW_Ridle, ROW_Solution, Row_Upload, ROW_Hint, Row_Visible, Row_Solved, ROW_Name}, ROW_User + "=?", new String[]{userName}, null, null, null, null);

        int user=c.getColumnIndex(ROW_User);
        int lat=c.getColumnIndex(ROW_Lat);
        int log=c.getColumnIndex(ROW_Log);
        int ridle=c.getColumnIndex(ROW_Ridle);
        int solution=c.getColumnIndex(ROW_Solution);
        int visi=c.getColumnIndex(Row_Visible);
        int tit=c.getColumnIndex(ROW_Name);
        int sol=c.getColumnIndex(Row_Solved);
        int up=c.getColumnIndex(Row_Upload);
        int hint=c.getColumnIndex(ROW_Hint);

        if(c!=null)
        {

            for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
            {

                places.add(new Place(c.getString(user),c.getString(lat),c.getString(log),c.getString(tit),c.getString(ridle),c.getString(solution),c.getString(hint),Boolean.parseBoolean(c.getString(visi)),Boolean.parseBoolean(c.getString(sol)),Boolean.parseBoolean(c.getString(up))));
            }

        }

        return places;
    }
    private static class DBHelper extends SQLiteOpenHelper
    {


        public DBHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase)
        {
            sqLiteDatabase.execSQL(CREATE_DATABASE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2)
        {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Places");
            onCreate(sqLiteDatabase);
        }
    }
}
