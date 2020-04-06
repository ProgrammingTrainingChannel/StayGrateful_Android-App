package com.btitsolutions.staygrateful.Utilities;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.btitsolutions.staygrateful.Models.GratitudeModel;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StayGratefulDB";
    private static final String TABLE_GRATITUDE = "tblGratitude";

    private static final String KEY_CODE = "code";
    private static final String KEY_CREATED_DATE = "createdDate";
    private static final String KEY_CONTENT = "content";

    Context _context;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GRATITUDE_TABLE = "CREATE TABLE " + TABLE_GRATITUDE + "("
                + KEY_CODE + " TEXT PRIMARY KEY, "
                + KEY_CREATED_DATE + " TEXT, "
                + KEY_CONTENT + " TEXT" + ")";

        db.execSQL(CREATE_GRATITUDE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRATITUDE);
        onCreate(db);
    }

    //for Gratitude table
    public void addGratitude(GratitudeModel gratitudeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, gratitudeModel.getCode());
        values.put(KEY_CREATED_DATE, gratitudeModel.getCreated_date());
        values.put(KEY_CONTENT, gratitudeModel.getContent());

        db.insert(TABLE_GRATITUDE, null, values);
        db.close();
    }

    public GratitudeModel getGratitude(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GRATITUDE, new String[] { KEY_CODE,
                        KEY_CREATED_DATE, KEY_CONTENT }, KEY_CODE + "=?",
                new String[] { String.valueOf(code) }, null, null, null, null);

        if (cursor != null)
        {
            cursor.moveToFirst();
        }

        GratitudeModel contact = new GratitudeModel(cursor.getString(0), cursor.getString(1), cursor.getString(2));
        return contact;
    }

    public List<GratitudeModel> getAllGratitudes() {
        List<GratitudeModel> gratitudeModels = new ArrayList<>();

        String selectQuery = "SELECT code, createdDate, content FROM " + TABLE_GRATITUDE + " ORDER BY CAST(code AS NUMERIC) DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                GratitudeModel gratitudeModel = new GratitudeModel();
                gratitudeModel.setCode(cursor.getString(0));
                gratitudeModel.setCreated_date(cursor.getString(1));
                gratitudeModel.setContent(cursor.getString(2));

                gratitudeModels.add(gratitudeModel);
            } while (cursor.moveToNext());
        }

        return gratitudeModels;
    }

    public int getGratitudesCount() {
        String countQuery = "SELECT * FROM " + TABLE_GRATITUDE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public int updateGratitude(GratitudeModel gratitudeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CODE, gratitudeModel.getCode());
        values.put(KEY_CREATED_DATE, gratitudeModel.getCreated_date());
        values.put(KEY_CONTENT, gratitudeModel.getContent());

        return db.update(TABLE_GRATITUDE, values, KEY_CODE + " = ?",
                new String[]{String.valueOf(gratitudeModel.getCode())});
    }

    public void deleteGratitude(GratitudeModel gratitudeModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GRATITUDE, KEY_CODE + " = ?",
                new String[] { gratitudeModel.getCode() });

        db.close();
    }
}