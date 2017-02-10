package com.ff.modealapplication.app.ui.search;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BIT on 2017-02-08.
 */

public class SearchDBManager extends SQLiteOpenHelper {
    public SearchDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE SEARCH_LIST( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public List<String> PrintData() {
        SQLiteDatabase db = getReadableDatabase();
        List<String> str = new ArrayList<String>();

        Cursor cursor = db.rawQuery("select DISTINCT name from SEARCH_LIST ", null);
        while (cursor.moveToNext()) {
            str.add(cursor.getString(0));
        }
        return str;
    }
}
