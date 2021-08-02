package ai.vision.visionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.ArrayList;

public class databaseHelper extends SQLiteOpenHelper
    {
    public static final String DATABASE_NAME = "data.db";
    public static final String VEICLES_TABLE_NAME = "veicles";
    public static final String VEICLES_COLUMN_ID = "id";
    public static final String VEICLES_TIME = "time";
    public static final String VEICLES_LAT = "lat";
    public static final String VEICLES_LON = "lon";


    public databaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table veicles" + "(id integer primary key,time long, lat double,lon double)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop Table IF EXISTS veicles");
        onCreate(db);
    }

    public void resetdb() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Drop Table IF EXISTS veicles");
        onCreate(db);
    }

    public void insertVeicle(Long time, double lat, double lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", time);
        contentValues.put("lat", lat);
        contentValues.put("lon", lon);
        db.insert("veicles", null, contentValues);
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from veicles where id=" + id + "", null);
        return res;
    }

    public int entries() {
        SQLiteDatabase db = this.getReadableDatabase();
        int num = (int) DatabaseUtils.queryNumEntries(db, "veicles");
        return num;
    }

    public ArrayList<Long> getveicletime() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Long> time = new ArrayList<Long>();

        Cursor res = db.rawQuery("select * from veicles where lat between -180 and 180", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            time.add(res.getLong(res.getColumnIndex("time")));
            res.moveToNext();
        }
        return time;
    }

    public ArrayList<Double> getveiclelat() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Double> lat = new ArrayList<Double>();

        Cursor res = db.rawQuery("select * from veicles where lat between -180 and 180",null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            lat.add(res.getDouble(res.getColumnIndex("time")));
            res.moveToNext();
        }
        return lat;
    }
    public ArrayList<Double> getveiclelon() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Double> lon = new ArrayList<Double>();

        Cursor res = db.rawQuery("select * from veicles where lon between 1 and 2000000000",null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            lon.add(res.getDouble(res.getColumnIndex("time")));
            res.moveToNext();
        }
        return lon;
    }

    }


