package org.activityRecognition.activityTracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class ActivityDB extends SQLiteOpenHelper {
    //DB columns
    public static final String dbname = "MyActivity.db";
    public static final String _id = "_id";
    public static final String activityname = "activityname";
    public static final String date = "date";
    public static final String time = "time";
    public static final String myactivities = "myactivities";
    private HashMap hp;
    SQLiteDatabase db;

    public ActivityDB(Context context) {
        super(context, dbname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table myactivities"
                + "(_id integer primary key, activityname text,date text,time text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + myactivities);
        onCreate(db);
    }
//    // to list all added notes
//    public Cursor fetchAll() {
//        db = this.getReadableDatabase();
//        Cursor mCursor = db.query(myactivities, new String[] { "_id", "activityname",
//                "date", "time"}, null, null, null, null, null);
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//    }

    //to add any new note
    public boolean addActivity(String activityname, String date, String time) {
        System.out.println("entered add activity" + activityname + date + time);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("activityname", activityname);
        contentValues.put("date", date);
        contentValues.put("time", time);
        db.insert(myactivities, null, contentValues);
        return true;
    }

    // to edit or view a single note
    public Cursor getActivity() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor z = db.rawQuery("select * from " + myactivities + "", null);
        return z;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, myactivities);
        return numRows;
    }

//    //to update any note
//    public boolean updateActivity(Integer id, String activityname, String date,
//                              String time, byte[] image_array) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", activityname);
//        contentValues.put("date", date);
//        contentValues.put("time", time);
//        db.update(myactivities, contentValues, "_id = ? ",
//                new String[] { Integer.toString(id) });
//        return true;
//    }

//    public Integer deleteActivity(Integer id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete(myactivities, "_id = ? ",
//                new String[] { Integer.toString(id) });
//    }

//    public ArrayList getAll() {
//        ArrayList array_list = new ArrayList();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res = db.rawQuery("select * from " + myactivities, null);
//        res.moveToFirst();
//        while (res.isAfterLast() == false) {
//            array_list.add(res.getString(res.getColumnIndex("_id")));
//            array_list.add(res.getString(res.getColumnIndex(time)));
//            array_list.add(res.getString(res.getColumnIndex(date)));
//            array_list.add(res.getString(res.getColumnIndex(activityname)));
//            res.moveToNext();
//        }
//        return array_list;
//    }

}
