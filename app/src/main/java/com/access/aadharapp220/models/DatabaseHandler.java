package com.access.aadharapp220.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "UserDB";

    // Contacts table name

    // Contacts Table Columns names
    public static final String KEY_ID = "ID";
    public static final String KEY_FNAME = "NAME";
    public static final String KEY_UID = "UID";
    public static final String KEY_FP1 = "FINGER1";
    public static final String KEY_FP2 = "FINGER2";
    public static String TABLE_NAME = "UserTB";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
      /* String CREATE_TABLE_CONTACTS="CREATE TABLE " + TABLE_NAME + "("
               + KEY_ID +" INTEGER PRIMARY KEY,"
               + KEY_FNAME +" TEXT,"
               + KEY_UID +" TEXT,"
               + KEY_FP1 +" BLOB,"
               + KEY_FP2 +" BLOB," + ")";
        db.execSQL(CREATE_TABLE_CONTACTS);*/

        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,UID TEXT,NAME TEXT,FINGER1 TEXT,FINGER2 TEXT)");
    }


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        /*db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);*/

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public void insertPerson(Contact contact) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_UID, contact.get_uid());
        contentValues.put(KEY_FNAME, contact.get_fname());
        contentValues.put(KEY_FP1, contact.get_fp1());
        contentValues.put(KEY_FP2, contact.get_fp2());

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }


    public  boolean insertData(String uid,String name,String fp1,String fp2)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(KEY_UID,uid);
        cv.put(KEY_FNAME,name);
        cv.put(KEY_FP1,fp1);
        cv.put(KEY_FP2,fp2);
        Cursor cursor = db.query(TABLE_NAME,null,KEY_UID+"=?",new String[]{uid},null,null,null);
        if(cursor.moveToFirst()&&cursor.getCount()>0){
            long result = db.update(TABLE_NAME,cv,KEY_UID+"=?",new String[]{uid});
            if (result == -1)
                return false;
            else
                return true;
        }else {
            long result = db.insert(TABLE_NAME, null, cv);
            if (result == -1)
                return false;
            else
                return true;
        }

    }

    public void delete(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,KEY_UID+"=?",new String[]{uid});
    }

    public Contact getData(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_UID,uid);
        Cursor cursor = db.query(TABLE_NAME,null,KEY_UID+"=?",new String[]{uid},null,null,null);
        Contact contact = new Contact();
        if(cursor.moveToFirst()&&cursor.getCount()>0){
            do{
                contact.set_uid(cursor.getString(1));
                contact.setFName(cursor.getString(2));
                contact.set_fp1(cursor.getString(3));
                contact.set_fp2(cursor.getString(4));
            }while (cursor.moveToNext());
        }
        if(!cursor.isClosed())cursor.close();
        return contact;

    }

    public boolean checkIfRecordExist(String TABLE_NAME,String KEY_UID,String chek)
    {
        try
        {
            SQLiteDatabase db=this.getReadableDatabase();
            Cursor cursor=db.rawQuery("SELECT "+KEY_UID+" FROM "+TABLE_NAME+" WHERE "+KEY_UID+"='"+KEY_UID+"'",null);
            if (cursor.moveToFirst())
            {
                db.close();
                Log.d("Record  Already Exists", "Table is:"+TABLE_NAME+" ColumnName:"+KEY_UID);
                return true;//record Exists

            }
            Log.d("New Record  ", "Table is:"+TABLE_NAME+" ColumnName:"+KEY_UID+" Column Value:"+KEY_UID);
            db.close();
        }
        catch(Exception errorException)
        {
            Log.d("Exception occured", "Exception occured "+errorException);
            // db.close();
        }
        return false;
    }

    public Cursor getContact(String KEY_UID, SQLiteDatabase sqLiteDatabase) {
        String[] projections = {KEY_UID,KEY_FNAME};
        String selection = TABLE_NAME+" LIKE ?";
        String[] seletion_args={KEY_UID};
        Cursor cursor=sqLiteDatabase.query(TABLE_NAME,projections,selection,seletion_args,null,null,null);
        return cursor;

    }

    public HashMap<String, String> getEmpData(String id) {
        HashMap<String, String> wordList = new HashMap<String, String>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM UserTB where UID='"+id+"'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                //HashMap<String, String> map = new HashMap<String, String>();
                wordList.put("UID",cursor.getString(1));
                wordList.put("NAME", cursor.getString(2));
                wordList.put("FINGER1", cursor.getString(3));
                wordList.put("Finger2", cursor.getString(4));
                //wordList.add(map);
            } while (cursor.moveToNext());
        }
        return wordList;
    }

   /* //getbankbal - ERROR here !
    public String getEmpSingleData() {
        String empresult;
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT NAME, FINGER1, FINGER2 FROM UserTB";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                empresult.put("NAME", cursor.getString(1));
                empresult.put("FINGER1", cursor.getString(2));
                empresult.put("FINGER2", cursor.getString(3));
            } while (cursor.moveToNext());
        }
        return empresult;
    }*/

    /*public Bitmap getBitmap(int id){
        Bitmap bitmap = null;
        // Open the database for reading
        SQLiteDatabase db = this.getReadableDatabase();
        // Start the transaction.
        db.beginTransaction();

        try
        {
            String selectQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE id = " + id;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if(cursor.getCount() >0)
            {
                while (cursor.moveToNext()) {
                    // Convert blob data to byte array
                    byte[] blob = cursor.getBlob(cursor.getColumnIndex("img"));
                    // Convert the byte array to Bitmap
                    bitmap= BitmapFactory.decodeByteArray(blob, 0, blob.length);

                }

            }
            db.setTransactionSuccessful();

        }
        catch (SQLiteException e)
        {
            e.printStackTrace();

        }
        finally
        {
            db.endTransaction();
            // End the transaction.
            db.close();
            // Close database
        }
        return bitmap;

    }*/

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */



    //Insert values to the table contacts
    public void addContacts(Contact contact){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values=new ContentValues();

        values.put(KEY_UID, contact.get_uid());
        values.put(KEY_FNAME, contact.get_fname());
        values.put(KEY_FP1, contact.get_fp1());
        values.put(KEY_FP2, contact.get_fp2());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }


}
