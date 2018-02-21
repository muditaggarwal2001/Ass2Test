package com.example.mudit.ass2test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mudit on 20-02-2018.
 */

public class DBHelperClass extends SQLiteOpenHelper {

    public static final String name = "activity.db";
    public static final String id = "_ID";
    public static final String title = "Title";
    public static final String time = "time";
    public static final String Table_Name = "activitytable";
    public static final int version = 1;

    SQLiteDatabase db;
    public DBHelperClass(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+Table_Name+" ("+id+" INTEGER Primary Key, "+title+" TEXT, "+time+" TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+Table_Name);
        onCreate(db);
    }

    public void open()
    {
        db=this.getWritableDatabase();
    }

    public void close()
    {
        db.close();
    }

    public Cursor getdata()
    {
        Cursor result = db.rawQuery("Select * from "+Table_Name, null);
        return result;
    }

    public long insertData(String ititle, String itime)
    {
        ContentValues values = new ContentValues();
        values.put(title,ititle);
        values.put(time,itime);
        long result=db.insert(Table_Name,null,values);
        return result;
    }

    public void truncate()
    {
        db.execSQL("DELETE FROM "+Table_Name);
    }
}
