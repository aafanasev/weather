package com.jokuskay.weather.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract;
import com.jokuskay.weather.helpers.DbColumn;
import com.jokuskay.weather.helpers.DbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Country {

    public static final String TABLE = "countries";

    public static enum Column implements DbColumn {
        _id("INTEGER PRIMARY KEY"), name("TEXT");

        private String mType;

        private Column(String type) {
            mType = type;
        }

        @Override
        public String getType() {
            return mType;
        }
    }

    private int mId;
    private String mName;

    public Country() {

    }

    public Country(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public static void removeAll(Context context) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE);
    }

    public static List<Country> getAll(Context context) {
        List<Country> result = new ArrayList<>();

        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE + " ORDER BY " + Column.name.name() + " ASC", null);
        int iId = c.getColumnIndex(Column._id.name());
        int iName = c.getColumnIndex(Column.name.name());

        if (c.moveToFirst()) {
            do {
                result.add(new Country(c.getInt(iId), c.getString(iName)));
            } while (c.moveToNext());
        }

        c.close();
        return result;
    }

    public static void save(Context context, Map<String, Integer> countries) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (Map.Entry<String, Integer> entry : countries.entrySet()) {
            ContentValues values = new ContentValues();
            values.put(Column._id.name(), entry.getValue());
            values.put(Column.name.name(), entry.getKey());
            db.insert(TABLE, null, values);
        }
    }

}
